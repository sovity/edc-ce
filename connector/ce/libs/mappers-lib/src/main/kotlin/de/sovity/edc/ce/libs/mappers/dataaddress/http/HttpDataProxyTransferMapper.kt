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
import de.sovity.edc.ce.libs.mappers.dataaddress.model.InitiateTransferParams
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop

@Service
class HttpDataProxyTransferMapper(
    private val httpDataAddressMapper: HttpDataAddressMapper
) {

    /**
     * Transfer Params for type HttpData-PROXY
     *
     * @param httpData [UiDataSinkHttpDataPush]
     * @return properties for [org.eclipse.edc.spi.types.domain.DataAddress]
     */
    fun buildTransferParams(): InitiateTransferParams {
        return InitiateTransferParams().also {
            it.dataSinkProperties += mapOf(Prop.Edc.TYPE to Prop.Edc.DATA_ADDRESS_TYPE_HTTP_PROXY)
            it.transferType = "HttpData-PULL"
        }
    }
}
