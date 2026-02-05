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

import de.sovity.edc.ce.api.common.model.UiDataSinkAzureStorage
import de.sovity.edc.ce.libs.mappers.dataaddress.model.InitiateTransferParams
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.spi.security.Vault

@Service
class AzureStorageDataSinkMapper(
    private val vault: Vault
) {
    fun buildAzureStorageTransferData(
        azureStorage: UiDataSinkAzureStorage
    ): InitiateTransferParams {
        if (vault.resolveSecret(azureStorage.storageAccountName + "-key1") == null) {
            error("The secret for the storage account must be stored in '${azureStorage.storageAccountName}-key1'.")
        }
        val dataAddress = mutableMapOf(
            Prop.Edc.TYPE to Prop.Edc.AZURE_BLOB_STORE_TYPE,
            Prop.Edc.AZURE_ACCOUNT_NAME to azureStorage.storageAccountName,
            Prop.Edc.AZURE_CONTAINER_NAME to azureStorage.containerName,
            Prop.Edc.AZURE_FOLDER_NAME to azureStorage.folderName,
            Prop.Edc.AZURE_BLOB_NAME to azureStorage.blobName,
        )
        return InitiateTransferParams().also {
            it.dataSinkProperties += dataAddress
            it.transferType = Prop.Edc.AZURE_BLOB_STORE_TYPE + "-PUSH"
        }
    }
}
