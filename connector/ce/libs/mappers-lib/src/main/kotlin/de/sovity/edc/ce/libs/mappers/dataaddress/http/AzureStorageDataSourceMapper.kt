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

import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.spi.types.domain.DataAddress

@Service
class AzureStorageDataSourceMapper {
    fun buildAzureStorageDataAddress(
        storageAccountName: String,
        containerName: String,
        blobName: String,
        accountKey: String
    ): Map<String, String> {
        return mapOf(
            Prop.Edc.TYPE to Prop.Edc.AZURE_BLOB_STORE_TYPE,
            Prop.Edc.AZURE_ACCOUNT_NAME to storageAccountName,
            Prop.Edc.AZURE_CONTAINER_NAME to containerName,
            Prop.Edc.AZURE_BLOB_NAME to blobName,
            DataAddress.EDC_DATA_ADDRESS_KEY_NAME to accountKey,
        )
    }
}
