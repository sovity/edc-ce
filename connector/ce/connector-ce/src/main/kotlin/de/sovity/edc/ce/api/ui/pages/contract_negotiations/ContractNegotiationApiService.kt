/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_negotiations

import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest
import de.sovity.edc.ce.api.ui.model.UiContractNegotiation
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.services.spi.contractnegotiation.ContractNegotiationService

@Service
class ContractNegotiationApiService(
    private val contractNegotiationService: ContractNegotiationService,
    private val contractNegotiationBuilder: ContractNegotiationBuilder,
    private val contractNegotiationStateService: ContractNegotiationStateService,
) {

    fun initiateContractNegotiation(request: ContractNegotiationRequest): UiContractNegotiation {
        val contractRequest = contractNegotiationBuilder.buildContractNegotiation(request)
        val contractNegotiation = contractNegotiationService.initiateNegotiation(contractRequest)
        return buildContractNegotiation(contractNegotiation)
    }

    fun getContractNegotiation(contractNegotiationId: String): UiContractNegotiation {
        val contractNegotiation = contractNegotiationService.findbyId(contractNegotiationId)
        return buildContractNegotiation(contractNegotiation)
    }

    private fun buildContractNegotiation(contractNegotiation: ContractNegotiation): UiContractNegotiation {
        val status = contractNegotiationStateService.buildContractNegotiationState(contractNegotiation.state)
        val agreementId = contractNegotiation.contractAgreement?.id
        return UiContractNegotiation(
            contractNegotiation.id,
            EdcDateUtils.utcMillisToOffsetDateTime(contractNegotiation.createdAt),
            agreementId,
            status
        )
    }
}
