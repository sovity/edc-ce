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
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest
import de.sovity.edc.ce.libs.mappers.PlaceholderEndpointService
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject
import java.util.Objects

@Service
class OnRequestDataSourceMapper(
    private val httpDataSourceMapper: HttpDataSourceMapper,
    private val placeholderEndpointService: PlaceholderEndpointService
) {
    fun buildOnRequestDataAddress(onRequest: UiDataSourceOnRequest): Map<String, String?> {
        val contactEmail = Objects.requireNonNull(onRequest.contactEmail, "contactEmail must not be null")
        val contactEmailSubject = Objects.requireNonNull(
            onRequest.contactPreferredEmailSubject,
            "Need contactPreferredEmailSubject"
        )

        val placeholderEndpointForAsset = placeholderEndpointService.getPlaceholderEndpointForAsset(
            onRequest.contactEmail,
            onRequest.contactPreferredEmailSubject
        )

        val actualDataSource = UiDataSourceHttpData.builder()
            .baseUrl(placeholderEndpointForAsset)
            .build()

        val props = httpDataSourceMapper.buildDataAddress(actualDataSource)
        props[Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY] =
            Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST
        props[Prop.SovityDcatExt.CONTACT_EMAIL] = contactEmail
        props[Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT] = contactEmailSubject
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

        // On Request information
        listOf(
            Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY,
            Prop.SovityDcatExt.CONTACT_EMAIL,
            Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT
        ).forEach {
            val value = dataAddress[it]
            if (!value.isNullOrBlank()) {
                json.add(it, value)
            }
        }

        return json.build()
    }

    fun isOnRequestDataAddress(dataAddress: Map<String, String?>): Boolean =
        Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST == dataAddress[Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY]
}
