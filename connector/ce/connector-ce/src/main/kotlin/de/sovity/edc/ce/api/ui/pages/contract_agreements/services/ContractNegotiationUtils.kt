/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services

import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService
import de.sovity.edc.ce.api.utils.ServiceException
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.services.spi.contractnegotiation.ContractNegotiationService
import org.eclipse.edc.spi.EdcException
import org.eclipse.edc.spi.query.Criterion
import org.eclipse.edc.spi.query.QuerySpec

@RequiredArgsConstructor
@Service
class ContractNegotiationUtils(
    private val selfDescriptionService: SelfDescriptionService,
    private val contractNegotiationService: ContractNegotiationService
) {

    fun findByContractAgreementIdOrThrow(contractAgreementId: String?): ContractNegotiation {
        val querySpec = QuerySpec.Builder.newInstance()
            .filter(listOf(Criterion("contractAgreement.id", "=", contractAgreementId)))
            .build()
        return contractNegotiationService.search(querySpec)
            .orElseThrow { ServiceException(it) }
            .firstOrNull() ?: throw EdcException(
            "Could not fetch contractNegotiation for contractAgreement $contractAgreementId"
        )
    }

    /**
     * Returns the asset provider's connector endpoint
     *
     * @param negotiation negotiation
     * @return participant ID
     */
    fun getProviderConnectorEndpoint(negotiationType: ContractNegotiation.Type, counterPartyAddress: String): String {
        if (negotiationType == ContractNegotiation.Type.PROVIDER) {
            return selfDescriptionService.connectorEndpoint
        }

        return counterPartyAddress
    }

    /**
     * Returns the asset provider's participant ID
     *
     * @param negotiation negotiation
     * @return participant ID
     */
    fun getProviderParticipantId(negotiationType: ContractNegotiation.Type, counterPartyId: String): String {
        if (negotiationType == ContractNegotiation.Type.PROVIDER) {
            return selfDescriptionService.participantId
        }

        return counterPartyId
    }
}
