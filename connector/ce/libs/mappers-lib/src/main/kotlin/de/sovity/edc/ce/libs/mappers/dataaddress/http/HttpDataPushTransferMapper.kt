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
package de.sovity.edc.ce.libs.mappers.dataaddress.http

import de.sovity.edc.ce.api.common.model.UiDataSinkHttpDataPush
import de.sovity.edc.ce.api.common.model.UiHttpAuth
import de.sovity.edc.ce.api.common.model.UiHttpAuthType
import de.sovity.edc.ce.api.common.model.UiHttpPushAuth
import de.sovity.edc.ce.api.common.model.UiHttpPushAuthType
import de.sovity.edc.ce.libs.mappers.dataaddress.model.InitiateTransferParams
import de.sovity.edc.runtime.simple_di.Service

@Service
class HttpDataPushTransferMapper(
    private val httpDataAddressMapper: HttpDataAddressMapper
) {

    /**
     * Transfer Params for type HttpData-PUSH
     *
     * @param httpData [UiDataSinkHttpDataPush]
     * @return properties for [org.eclipse.edc.spi.types.domain.DataAddress]
     */
    fun buildTransferParams(httpData: UiDataSinkHttpDataPush): InitiateTransferParams {
        val dataAddress = httpDataAddressMapper.buildDataAddress(
            baseUrl = httpData.baseUrl,
            method = httpData.method?.name,
            queryString = httpData.queryString,
            auth = mapAuth(httpData.auth),
            headers = httpData.headers
        )
        return InitiateTransferParams().also {
            it.dataSinkProperties += dataAddress
            it.transferType = "HttpData-PUSH"
        }
    }

    private fun mapAuth(auth: UiHttpPushAuth?): UiHttpAuth? {
        if (auth == null) {
            return null
        }

        val type = when (auth.type!!) {
            UiHttpPushAuthType.BASIC -> UiHttpAuthType.BASIC
            UiHttpPushAuthType.API_KEY -> UiHttpAuthType.API_KEY
        }

        return UiHttpAuth(
            type,
            auth.basic,
            auth.apiKey,
            null
        )
    }
}
