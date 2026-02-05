/*
 * Copyright 2025 sovity GmbH
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
 */
package de.sovity.edc.ce.libs.mappers.dataaddress

import de.sovity.edc.ce.api.common.model.UiInitiateTransferRequest
import de.sovity.edc.ce.api.common.model.UiInitiateTransferType
import de.sovity.edc.ce.libs.mappers.dataaddress.http.AzureStorageDataSinkMapper
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataProxyTransferMapper
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataPushTransferMapper
import de.sovity.edc.ce.libs.mappers.dataaddress.model.InitiateTransferParams
import de.sovity.edc.runtime.simple_di.Service

@Service
class InitiateTransferRequestMapper(
    private val httpDataPushTransferMapper: HttpDataPushTransferMapper,
    private val httpDataProxyTransferMapper: HttpDataProxyTransferMapper,
    private val azureStorageDataSinkMapper: AzureStorageDataSinkMapper
) {

    /**
     * Maps between [UiInitiateTransferRequest] and [InitiateTransferParams].
     */
    fun buildInitiateTransferParams(request: UiInitiateTransferRequest): InitiateTransferParams {
        val params = when (request.type ?: UiInitiateTransferType.CUSTOM) {
            UiInitiateTransferType.HTTP_DATA_PUSH ->
                httpDataPushTransferMapper.buildTransferParams(request.httpDataPush)

            UiInitiateTransferType.HTTP_DATA_PROXY ->
                httpDataProxyTransferMapper.buildTransferParams()

            UiInitiateTransferType.AZURE_STORAGE ->
                azureStorageDataSinkMapper.buildAzureStorageTransferData(request.azureStorage)

            UiInitiateTransferType.CUSTOM ->
                InitiateTransferParams()
        }

        if (request.customTransferType != null) {
            params.transferType = request.customTransferType
        }

        if (request.customDataSinkProperties != null) {
            params.dataSinkProperties += request.customDataSinkProperties
        }

        if (request.customTransferPrivateProperties != null) {
            params.transferPrivateProperties += request.customTransferPrivateProperties
        }

        return params
    }
}
