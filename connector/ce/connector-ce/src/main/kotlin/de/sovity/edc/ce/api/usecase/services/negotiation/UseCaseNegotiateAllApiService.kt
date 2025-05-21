/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.negotiation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.ce.api.common.model.CatalogQueryV2
import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest
import de.sovity.edc.ce.api.ui.model.UiContractNegotiation
import de.sovity.edc.ce.api.ui.model.UiContractOffer
import de.sovity.edc.ce.api.ui.model.UiDataOffer
import de.sovity.edc.ce.api.ui.pages.contract_negotiations.ContractNegotiationApiService
import de.sovity.edc.ce.api.ui.pages.contract_negotiations.ContractNegotiationStateService
import de.sovity.edc.ce.api.usecase.model.NegotiateAllQuery
import de.sovity.edc.ce.api.usecase.model.NegotiateAllResult
import de.sovity.edc.ce.api.usecase.services.catalog.UseCaseCatalogV2ApiService
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.eqAny
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.libs.mappers.AssetMapper
import de.sovity.edc.ce.libs.mappers.PolicyMapper
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer
import org.jooq.DSLContext
import org.jooq.JSON

@Service
class UseCaseNegotiateAllApiService(
    private val useCaseCatalogV2ApiService: UseCaseCatalogV2ApiService,
    private val contractNegotiationApiService: ContractNegotiationApiService,
    private val contractNegotiationStateService: ContractNegotiationStateService,
    private val policyMapper: PolicyMapper,
    private val objectMapper: ObjectMapper,
    private val assetMapper: AssetMapper
) {

    fun negotiateAll(
        dsl: DSLContext,
        query: NegotiateAllQuery
    ): List<NegotiateAllResult> {
        val dataOffers = useCaseCatalogV2ApiService.fetchDataOffers(
            CatalogQueryV2.builder()
                .offset(0)
                .limit(query.limit)
                .filter(query.filter)
                .participantId(query.participantId)
                .connectorEndpoint(query.connectorEndpoint)
                .build()
        )

        val existingNegotiations = queryExistingNegotiations(
            dsl,
            query.participantId,
            query.connectorEndpoint,
            assetIds = dataOffers.map { it.asset.assetId }
        )

        val existingNegotiationsMapped = existingNegotiations.map { negotiation ->
            buildExistingNegotiationResult(negotiation, query)
        }

        val existingNegotiationsByAssetId = existingNegotiations.map { it.assetId }.toSet()
        val newNegotiationsMapped = dataOffers
            .filter { it.asset.assetId !in existingNegotiationsByAssetId }
            .map { dataOffer ->
                // We just select the first one
                // This kind of 'one-shot negotiate' does not have any sort of
                // policy handling
                val contractOffer = dataOffer.contractOffers.first()

                val negotiation = initiateNegotiation(query, contractOffer, dataOffer)

                buildNewNegotiationResult(negotiation, dataOffer, contractOffer)
            }

        return existingNegotiationsMapped + newNegotiationsMapped
    }

    private fun buildExistingNegotiationResult(
        negotiation: NegotiationRs,
        query: NegotiateAllQuery
    ): NegotiateAllResult {
        val contractOffer = objectMapper.readValue(
            negotiation.contractOffersSerialized.data(),
            object : TypeReference<List<ContractOffer>>() {}
        ).single()

        val asset = assetMapper.buildUiAsset(
            Asset.Builder.newInstance().id(negotiation.assetId).build(),
            query.connectorEndpoint,
            query.participantId
        )

        val policy = policyMapper.buildUiPolicy(contractOffer.policy)

        return NegotiateAllResult.builder()
            .contractNegotiationId(negotiation.contractNegotiationId)
            .contractAgreementId(negotiation.contractAgreementId)
            .state(contractNegotiationStateService.buildContractNegotiationState(negotiation.state))
            .stateChangedAt(EdcDateUtils.utcMillisToOffsetDateTime(negotiation.stateChangeAt))
            .asset(asset)
            .policy(policy)
            .build()
    }

    private fun buildNewNegotiationResult(
        negotiation: UiContractNegotiation,
        dataOffer: UiDataOffer,
        contractOffer: UiContractOffer
    ): NegotiateAllResult = NegotiateAllResult.builder()
        .contractNegotiationId(negotiation.contractNegotiationId)
        .contractAgreementId(negotiation.contractAgreementId)
        .state(negotiation.state)
        .stateChangedAt(negotiation.createdAt)
        .asset(dataOffer.asset)
        .policy(contractOffer.policy)
        .build()

    private fun initiateNegotiation(
        query: NegotiateAllQuery,
        contractOffer: UiContractOffer,
        dataOffer: UiDataOffer
    ) = contractNegotiationApiService.initiateContractNegotiation(
        ContractNegotiationRequest.builder()
            .counterPartyId(query.participantId)
            .counterPartyAddress(query.connectorEndpoint)
            .callbackAddresses(query.callbackAddresses)
            .policyJsonLd(contractOffer.policy.policyJsonLd)
            .contractOfferId(contractOffer.contractOfferId)
            .assetId(dataOffer.asset.assetId)
            .build()
    )

    private data class NegotiationRs(
        val contractNegotiationId: String,
        val contractAgreementId: String?,
        val assetId: String,
        val state: Int,
        val stateChangeAt: Long,
        val contractOffersSerialized: JSON,
        val counterpartyConnectorEndpoint: String,
        val counterpartyId: String
    )

    private fun queryExistingNegotiations(
        dsl: DSLContext,
        participantId: String,
        connectorEndpoint: String,
        assetIds: Collection<String>
    ): List<NegotiationRs> {
        val negotiation = Tables.EDC_CONTRACT_NEGOTIATION
        val termination = Tables.SOVITY_CONTRACT_TERMINATION

        return dsl.select(
            NegotiationRs::contractNegotiationId from {
                negotiation.ID
            },
            NegotiationRs::contractAgreementId from {
                negotiation.AGREEMENT_ID
            },
            NegotiationRs::assetId from {
                negotiation.getAssetId()
            },
            NegotiationRs::state from {
                negotiation.STATE
            },
            NegotiationRs::stateChangeAt from {
                negotiation.STATE_TIMESTAMP
            },
            NegotiationRs::contractOffersSerialized from {
                negotiation.CONTRACT_OFFERS
            },
            NegotiationRs::counterpartyConnectorEndpoint from {
                negotiation.COUNTERPARTY_ADDRESS
            },
            NegotiationRs::counterpartyId from {
                negotiation.COUNTERPARTY_ID
            },
        )
            .from(negotiation)
            .leftJoin(termination).on(termination.CONTRACT_AGREEMENT_ID.eq(negotiation.AGREEMENT_ID))
            .where(
                negotiation.COUNTERPARTY_ID.eq(participantId),
                negotiation.COUNTERPARTY_ADDRESS.eq(connectorEndpoint),
                contractNegotiationStateService.isOkDb(negotiation),
                termination.CONTRACT_AGREEMENT_ID.isNull,
                negotiation.TYPE.eq(ContractNegotiation.Type.CONSUMER.name),
                negotiation.getAssetId().eqAny(assetIds.toSet()),
            )
            .fetchInto(NegotiationRs::class.java)
    }
}
