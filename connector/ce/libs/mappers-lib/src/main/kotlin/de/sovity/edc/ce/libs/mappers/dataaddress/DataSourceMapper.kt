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

import de.sovity.edc.ce.api.common.model.DataSourceType
import de.sovity.edc.ce.api.common.model.UiDataSource
import de.sovity.edc.ce.libs.mappers.asset.utils.EdcPropertyUtils
import de.sovity.edc.ce.libs.mappers.dataaddress.http.AzureStorageDataSourceMapper
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataSourceMapper
import de.sovity.edc.ce.libs.mappers.dataaddress.http.OnRequestDataSourceMapper
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.JsonLdUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject
import jakarta.json.JsonValue

@Service
class DataSourceMapper(
    private val edcPropertyUtils: EdcPropertyUtils,
    private val httpDataSourceMapper: HttpDataSourceMapper,
    private val onRequestDataSourceMapper: OnRequestDataSourceMapper,
    private val azureStorageDataSourceMapper: AzureStorageDataSourceMapper
) {

    fun buildDataSourceJsonLd(dataSource: UiDataSource): JsonObject {
        var type = dataSource.type
        if (type == null) {
            type = DataSourceType.CUSTOM
        }

        var props = when (type) {
            DataSourceType.HTTP_DATA ->
                httpDataSourceMapper.buildDataAddress(dataSource.httpData!!)

            DataSourceType.ON_REQUEST ->
                onRequestDataSourceMapper.buildOnRequestDataAddress(dataSource.onRequest!!)

            DataSourceType.AZURE_STORAGE -> azureStorageDataSourceMapper.buildAzureStorageDataAddress(
                storageAccountName = dataSource.azureStorage.storageAccountName,
                containerName = dataSource.azureStorage.containerName,
                blobName = dataSource.azureStorage.blobName,
                accountKey = dataSource.azureStorage.accountKey
            )

            DataSourceType.CUSTOM -> emptyMap()
        }

        // apply overrides
        props = props + (dataSource.customProperties ?: mapOf())

        return buildDataAddressJsonLd(props)
    }

    fun buildAssetPropsFromDataAddress(dataAddressJsonLd: JsonObject): JsonObject {
        // We purposefully do not match the DataSource type but the properties to support the data address type "CUSTOM"
        val dataAddress = parseDataAddressJsonLd(dataAddressJsonLd)
        val type = dataAddress.getOrDefault(Prop.Edc.TYPE, "")

        if (type == Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA) {
            // ON_REQUEST
            if (onRequestDataSourceMapper.isOnRequestDataAddress(dataAddress)) {
                return onRequestDataSourceMapper.enhanceAssetWithDataSourceHints(dataAddress)
            }

            // HTTP_DATA
            return httpDataSourceMapper.enhanceAssetWithDataSourceHints(dataAddress)
        }

        return JsonValue.EMPTY_JSON_OBJECT
    }

    private fun buildDataAddressJsonLd(properties: Map<String, String?>): JsonObject {
        val props = edcPropertyUtils.toMapOfObject(properties)
        return Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .addAll(Json.createObjectBuilder(props))
            .build()
    }

    private fun parseDataAddressJsonLd(dataAddressJsonLd: JsonObject): Map<String, String> =
        dataAddressJsonLd.mapValues { JsonLdUtils.string(it.value) ?: "" }
}
