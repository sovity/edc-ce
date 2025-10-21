/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements

import de.sovity.edc.ce.api.common.model.UiInitiateTransferRequest
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.model.InitiateCustomTransferRequest
import de.sovity.edc.ce.api.ui.model.InitiateTransferRequest
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.TransferRequestBuilder
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferRequest
import org.eclipse.edc.web.spi.exception.ServiceResultHandler
import org.jooq.DSLContext

@Service
class InitiateTransferApiService(
    private val transferRequestBuilder: TransferRequestBuilder,
    private val transferProcessService: TransferProcessService,
) {

    fun initiateTransferV2(request: UiInitiateTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildTransferRequestV2(request)
        return initiate(transferRequest)
    }

    @Deprecated("")
    fun initiateTransfer(request: InitiateTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildTransferRequest(request)
        return initiate(transferRequest)
    }

    fun initiateCustomTransfer(dsl: DSLContext, request: InitiateCustomTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildCustomTransferRequest(dsl, request)
        return initiate(transferRequest)
    }

    private fun initiate(transferRequest: TransferRequest): IdResponseDto {
        val transferProcess = transferProcessService.initiateTransfer(transferRequest)
            .orElseThrow(ServiceResultHandler.exceptionMapper(TransferProcess::class.java, transferRequest.id))
        return IdResponseDto(transferProcess.id)
    }
}
