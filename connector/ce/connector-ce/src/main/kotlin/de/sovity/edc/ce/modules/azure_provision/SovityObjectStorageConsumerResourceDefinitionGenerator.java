/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.azure_provision;

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
                .blobName(destination.getStringProperty(AzureBlobStoreSchema.BLOB_NAME));
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
