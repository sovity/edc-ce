/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements

import de.sovity.edc.ce.api.ui.model.ContractTerminationRequest
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractAgreementTerminationService
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractTerminationParam
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.EdcException
import org.jooq.DSLContext

@Service
class TerminateContractApiService(
    private val contractAgreementTerminationService: ContractAgreementTerminationService
) {
    fun terminate(
        dsl: DSLContext,
        contractAgreementId: String,
        contractTerminationRequest: ContractTerminationRequest
    ): IdResponseDto {
        try {
            val terminatedAt = contractAgreementTerminationService.terminateAgreementOrThrow(
                dsl,
                ContractTerminationParam(
                    contractAgreementId,
                    contractTerminationRequest.detail,
                    contractTerminationRequest.reason
                )
            )

            return IdResponseDto.builder()
                .id(contractAgreementId)
                .lastUpdatedDate(terminatedAt)
                .build()
        } catch (e: EdcException) {
            throw EdcException(
                "Failed to terminate the agreement $contractAgreementId: ${e.message}",
                e
            )
        }
    }
}
