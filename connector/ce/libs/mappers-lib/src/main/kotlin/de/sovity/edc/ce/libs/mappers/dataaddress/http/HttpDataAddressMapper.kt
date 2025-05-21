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

import de.sovity.edc.ce.api.common.model.SecretValue
import de.sovity.edc.ce.api.common.model.UiDataSinkHttpDataPush
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop

@Service
class HttpDataAddressMapper(
    private val httpHeaderMapper: HttpHeaderMapper
) {

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData [UiDataSinkHttpDataPush]
     * @return properties for [org.eclipse.edc.spi.types.domain.DataAddress]
     */
    fun buildDataAddress(
        baseUrl: String?,
        method: String?,
        queryString: String?,
        authHeaderName: String?,
        authHeaderValue: SecretValue?,
        headers: Map<String, String?>?
    ): MutableMap<String, String?> {
        val props = mutableMapOf<String, String?>(
            Prop.Edc.TYPE to Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA
        )

        baseUrl?.let { props[Prop.Edc.BASE_URL] = it }
        method?.let { props[Prop.Edc.METHOD] = it }

        if (!queryString.isNullOrBlank()) {
            props[Prop.Edc.QUERY_PARAMS] = queryString
        }

        if (!authHeaderName.isNullOrBlank()) {
            props[Prop.Edc.AUTH_KEY] = authHeaderName
            if (authHeaderValue != null) {
                if (authHeaderValue.rawValue != null) {
                    props[Prop.Edc.AUTH_CODE] = authHeaderValue.rawValue
                } else if (authHeaderValue.secretName != null) {
                    props[Prop.Edc.SECRET_NAME] = authHeaderValue.secretName
                }
            }
        }

        props.putAll(httpHeaderMapper.buildHeaderProps(headers))

        return props
    }
}
