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

import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject

@Service
class HttpDataSourceMapper(
    private val httpDataAddressMapper: HttpDataAddressMapper
) {

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData [UiDataSourceHttpData]
     * @return properties for [org.eclipse.edc.spi.types.domain.DataAddress]
     */
    fun buildDataAddress(httpData: UiDataSourceHttpData): MutableMap<String, String?> {
        require(httpData.baseUrl != null) { "baseUrl must not be null" }

        val props = httpDataAddressMapper.buildDataAddress(
            baseUrl = httpData.baseUrl,
            method = httpData.method?.name,
            queryString = httpData.queryString,
            auth = httpData.auth,
            headers = httpData.headers ?: emptyMap(),
        )

        // Parameterization
        if (true == httpData.enableMethodParameterization) {
            props[Prop.Edc.PROXY_METHOD] = "true"
        }
        if (true == httpData.enablePathParameterization) {
            props[Prop.Edc.PROXY_PATH] = "true"
        }
        if (true == httpData.enableQueryParameterization) {
            props[Prop.Edc.PROXY_QUERY_PARAMS] = "true"
        }
        if (true == httpData.enableBodyParameterization) {
            props[Prop.Edc.PROXY_BODY] = "true"
        }

        return props
    }

    /**
     * Public information from Data Address
     *
     * @param dataAddress data address
     * @return json object to be merged with asset properties
     */
    fun enhanceAssetWithDataSourceHints(dataAddress: Map<String, String?>): JsonObject {
        val json = Json.createObjectBuilder()

        mapOf(
            Prop.Edc.PROXY_METHOD to Prop.SovityDcatExt.HttpDatasourceHints.METHOD,
            Prop.Edc.PROXY_PATH to Prop.SovityDcatExt.HttpDatasourceHints.PATH,
            Prop.Edc.PROXY_QUERY_PARAMS to Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS,
            Prop.Edc.PROXY_BODY to Prop.SovityDcatExt.HttpDatasourceHints.BODY
        ).forEach { (prop, hint) ->  // Will add hints as "true" or "false"
            json.add(hint, ("true" == dataAddress[prop]).toString())
        }

        return json.build()
    }
}
