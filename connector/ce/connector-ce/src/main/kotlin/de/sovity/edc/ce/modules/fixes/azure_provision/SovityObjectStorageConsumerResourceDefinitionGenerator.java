/*
 * Copyright 2022 Microsoft Corporation
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
 *     Microsoft Corporation - initial API and implementation
 *     sovity GmbH - modifications
 */

package de.sovity.edc.ce.modules.fixes.azure_provision;

import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import org.eclipse.edc.azure.blob.AzureBlobStoreSchema;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.TransferTypeParser;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ConsumerResourceDefinitionGenerator;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.provision.azure.blob.ObjectStorageConsumerResourceDefinitionGenerator;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.jetbrains.annotations.Nullable;

import static java.util.UUID.randomUUID;

@MigrationSensitive(changes = "Use fixed ObjectStorageResourceDefinition. Will be fixed as of v0.8.1-72-g250dd26",
    dependsOn = ObjectStorageConsumerResourceDefinitionGenerator.class)
public class SovityObjectStorageConsumerResourceDefinitionGenerator implements ConsumerResourceDefinitionGenerator {

    private final TransferTypeParser transferTypeParser;

    public SovityObjectStorageConsumerResourceDefinitionGenerator(TransferTypeParser transferTypeParser) {
        this.transferTypeParser = transferTypeParser;
    }

    @Override
    public @Nullable ResourceDefinition generate(TransferProcess transferProcess, Policy policy) {
        // Use sovity variant of ObjectStorageResourceDefinition
        var definitionBuilder = SovityObjectStorageResourceDefinition.Builder.newInstance()
            .id(randomUUID().toString())
            .containerName(randomUUID().toString())
            .accountName(randomUUID().toString());

        var destination = transferProcess.getDataDestination();
        if (destination != null) {
            definitionBuilder
                .accountName(destination.getStringProperty(AzureBlobStoreSchema.ACCOUNT_NAME))
                .containerName(destination.getStringProperty(AzureBlobStoreSchema.CONTAINER_NAME, randomUUID().toString()))
                .folderName(destination.getStringProperty(AzureBlobStoreSchema.FOLDER_NAME))
                .blobName(destination.getStringProperty(AzureBlobStoreSchema.BLOB_NAME))
                // Vault alias for the storage account key, carried from the sink DataAddress' keyName.
                // Required when the transfer is initiated via the UI API Wrapper (UiDataSinkAzureStorage#accountKey).
                .keyName(destination.getKeyName());
        }

        return definitionBuilder.build();
    }

    @Override
    public boolean canGenerate(TransferProcess transferProcess, Policy policy) {
        return transferTypeParser.parse(transferProcess.getTransferType())
            .map(TransferType::destinationType)
            .map(AzureBlobStoreSchema.TYPE::equals)
            .orElse(failure -> false);
    }
}
