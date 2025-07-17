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

import de.sovity.edc.ce.api.common.model.UiHttpAuth
import de.sovity.edc.ce.api.common.model.UiHttpAuthApiKey
import de.sovity.edc.ce.api.common.model.UiHttpAuthBasic
import de.sovity.edc.ce.api.common.model.UiHttpAuthOauth2
import de.sovity.edc.ce.api.common.model.UiHttpAuthType.API_KEY
import de.sovity.edc.ce.api.common.model.UiHttpAuthType.BASIC
import de.sovity.edc.ce.api.common.model.UiHttpAuthType.OAUTH2
import de.sovity.edc.ce.api.common.model.UiHttpOauth2AuthType
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.ws.rs.core.HttpHeaders
import org.eclipse.edc.iam.oauth2.spi.Oauth2DataAddressSchema
import kotlin.io.encoding.Base64

@Service
class HttpDataAddressMapper(
    private val httpHeaderMapper: HttpHeaderMapper
) {

    /**
     * Data Address for type HTTP_DATA
     *
     * @return properties for [org.eclipse.edc.spi.types.domain.DataAddress]
     */
    fun buildDataAddress(
        baseUrl: String?,
        method: String?,
        queryString: String?,
        auth: UiHttpAuth?,
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

        props.putAll(httpHeaderMapper.buildHeaderProps(headers))

        val type = auth?.type
        if (type != null) {
            when (type) {
                API_KEY -> addApiKeyProps(auth.apiKey, props)
                BASIC -> addBasicProps(auth.basic, props)
                OAUTH2 -> addOauthProps(auth.oauth, props)
            }
        }

        return props
    }

    private fun addApiKeyProps(
        apiKey: UiHttpAuthApiKey,
        props: MutableMap<String, String?>
    ) {
        props[Prop.Edc.AUTH_KEY] = apiKey.headerName
        props[Prop.Edc.SECRET_NAME] = apiKey.vaultKey
    }

    private fun addBasicProps(
        basic: UiHttpAuthBasic,
        props: MutableMap<String, String?>
    ) {
        props[Prop.Edc.AUTH_KEY] = HttpHeaders.AUTHORIZATION
        val base64Creds = Base64.encode("${basic.username}:${basic.password}".toByteArray())
        props[Prop.Edc.AUTH_CODE] = "Basic $base64Creds"
    }

    private fun addOauthProps(
        oauth: UiHttpAuthOauth2,
        props: MutableMap<String, String?>
    ) {
        props[Oauth2DataAddressSchema.TOKEN_URL] = oauth.tokenUrl
        oauth.scope?.let {
            props[Oauth2DataAddressSchema.SCOPE] =it
        }

        val type = oauth.type!!
        when (type) {
            UiHttpOauth2AuthType.SHARED_SECRET -> {
                props[Oauth2DataAddressSchema.CLIENT_ID] = oauth.sharedSecret.clientId
                props[Oauth2DataAddressSchema.CLIENT_SECRET_KEY] = oauth.sharedSecret.clientSecretName
            }

            UiHttpOauth2AuthType.PRIVATE_KEY -> {
                props[Oauth2DataAddressSchema.PRIVATE_KEY_NAME] = oauth.privateKey.privateKeyName
                props[Oauth2DataAddressSchema.VALIDITY] = oauth.privateKey.tokenValidityInSeconds.toString()
            }
        }
    }
}
