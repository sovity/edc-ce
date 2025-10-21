/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_detail_page.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.ce.api.ui.model.ContractAgreementTerminationInfo
import de.sovity.edc.ce.api.ui.model.ContractAgreementTransferProcess
import de.sovity.edc.ce.api.ui.model.ContractDetailPageResult
import de.sovity.edc.ce.api.ui.model.ContractTerminationStatus
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.getDirection
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferProcessStateService
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy
import de.sovity.edc.ce.libs.mappers.PolicyMapper
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.policy.model.Policy

@Service
class ContractDetailPageBuilder(
    private val policyMapper: PolicyMapper,
    private val objectMapper: ObjectMapper,
    private val transferProcessStateService: TransferProcessStateService,
    private val contractDetailPageAssetBuilder: ContractDetailPageAssetBuilder
) {
    fun buildContractDetailPage(
        agreement: ContractDetailPageRs
    ): ContractDetailPageResult {
        val negotiationType = ContractNegotiation.Type.valueOf(agreement.negotiationType)
        val policy = objectMapper.readValue(
            agreement.policy.data(),
            object : TypeReference<Policy>() {}
        )

        return ContractDetailPageResult.builder().contractAgreementId(agreement.contractAgreementId)
            .contractNegotiationId(agreement.negotiationId)
            .direction(negotiationType.getDirection())
            .counterPartyAddress(agreement.negotiationCounterPartyAddress)
            .counterPartyId(agreement.negotiationCounterPartyId)
            .contractSigningDate(agreement.contractSigningDate)
            .asset(contractDetailPageAssetBuilder.buildUiAsset(agreement))
            .contractPolicy(policyMapper.buildUiPolicy(policy))
            .transferProcesses(buildTransferProcesses(agreement.transferProcesses))
            .terminationStatus(
                if (agreement.terminatedAt == null)
                    ContractTerminationStatus.ONGOING
                else ContractTerminationStatus.TERMINATED
            )
            .terminationInformation(
                agreement.terminatedBy?.let {
                    ContractAgreementTerminationInfo.builder()
                        .terminatedAt(agreement.terminatedAt)
                        .terminatedBy(
                            when (agreement.terminatedBy) {
                                ContractTerminatedBy.SELF ->
                                    de.sovity.edc.ce.api.ui.model.ContractTerminatedBy.SELF

                                ContractTerminatedBy.COUNTERPARTY ->
                                    de.sovity.edc.ce.api.ui.model.ContractTerminatedBy.COUNTERPARTY
                            }
                        )
                        .reason(agreement.terminationReason)
                        .detail(agreement.terminationDetail)
                        .build()
                }
            )
            .build()
    }

    private fun buildTransferProcesses(
        transferProcesses: List<ContractDetailPageTransferProcessRs>
    ) = transferProcesses
        .map {
            ContractAgreementTransferProcess().toBuilder()
                .transferProcessId(it.transferProcessId)
                .lastUpdatedDate(EdcDateUtils.utcMillisToOffsetDateTime(it.updatedAt))
                .state(transferProcessStateService.buildTransferProcessState(it.state))
                .errorMessage(it.errorDetail)
                .build()
        }
        .sortedBy { it.lastUpdatedDate }
        .reversed()
}
