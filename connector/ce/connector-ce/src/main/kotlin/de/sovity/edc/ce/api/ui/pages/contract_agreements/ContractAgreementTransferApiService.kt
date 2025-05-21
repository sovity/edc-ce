/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
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

@Service
class ContractAgreementTransferApiService(
    private val transferRequestBuilder: TransferRequestBuilder,
    private val transferProcessService: TransferProcessService
){

    fun initiateTransferV2(request: UiInitiateTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildTransferRequestV2(request)
        return initiate(transferRequest)
    }

    @Deprecated("")
    fun initiateTransfer(request: InitiateTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildTransferRequest(request)
        return initiate(transferRequest)
    }

    fun initiateCustomTransfer(request: InitiateCustomTransferRequest): IdResponseDto {
        val transferRequest = transferRequestBuilder.buildCustomTransferRequest(request)
        return initiate(transferRequest)
    }

    private fun initiate(transferRequest: TransferRequest): IdResponseDto {
        val transferProcess = transferProcessService.initiateTransfer(transferRequest)
            .orElseThrow(ServiceResultHandler.exceptionMapper(TransferProcess::class.java, transferRequest.id))
        return IdResponseDto(transferProcess.id)
    }
}
