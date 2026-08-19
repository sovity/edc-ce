/*
 * Copyright 2022 Microsoft Corporation
 * Copyright 2026 sovity GmbH
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

package de.sovity.edc.ce.modules.fixes.azure_storage;

import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import org.eclipse.edc.azure.blob.AzureBlobStoreSchema;
import org.eclipse.edc.azure.blob.AzureSasToken;
import org.eclipse.edc.azure.blob.api.BlobStoreApi;
import org.eclipse.edc.connector.dataplane.azure.storage.DestinationBlobName;
import org.eclipse.edc.connector.dataplane.azure.storage.metadata.BlobMetadataProvider;
import org.eclipse.edc.connector.dataplane.azure.storage.pipeline.AzureStorageDataSinkFactory;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSink;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSinkFactory;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.ACCOUNT_NAME;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.BLOB_NAME;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.BLOB_PREFIX;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.CONTAINER_NAME;
import static org.eclipse.edc.azure.blob.validator.AzureStorageValidator.validateAccountName;
import static org.eclipse.edc.azure.blob.validator.AzureStorageValidator.validateContainerName;
import static org.eclipse.edc.azure.blob.validator.AzureStorageValidator.validateKeyName;

/**
 * Instantiates {@link SovityAzureStorageDataSink}s for requests whose source data type is {@link AzureBlobStoreSchema#TYPE}.
 */
@MigrationSensitive(changes = "Use fixed AzureStorageDataSink, which will no longer create a completion marker file." +
    " Will be fixed as of EDC version 0.13.0",
    dependsOn = AzureStorageDataSinkFactory.class)
public class SovityAzureStorageDataSinkFactory implements DataSinkFactory {
    private final BlobStoreApi blobStoreApi;
    private final ExecutorService executorService;
    private final int partitionSize;
    private final Monitor monitor;
    private final Vault vault;
    private final TypeManager typeManager;
    private final BlobMetadataProvider metadataProvider;

    public SovityAzureStorageDataSinkFactory(BlobStoreApi blobStoreApi, ExecutorService executorService, int partitionSize, Monitor monitor, Vault vault, TypeManager typeManager, BlobMetadataProvider metadataProvider) {
        this.blobStoreApi = blobStoreApi;
        this.executorService = executorService;
        this.partitionSize = partitionSize;
        this.monitor = monitor;
        this.vault = vault;
        this.typeManager = typeManager;
        this.metadataProvider = metadataProvider;
    }

    @Override
    public String supportedType() {
        return AzureBlobStoreSchema.TYPE;
    }

    @Override
    public DataSink createSink(DataFlowStartMessage request) {
        var validate = validateRequest(request);
        if (validate.failed()) {
            throw new EdcException(validate.getFailure().getMessages().toString());
        }

        var dataAddress = request.getDestinationDataAddress();
        var requestId = request.getId();

        var secret = vault.resolveSecret(dataAddress.getKeyName());

        if (secret == null) {
            throw new EdcException("SAS token for the Azure Blob DataSink not found in Vault (alias = '%s')".formatted(dataAddress.getKeyName()));
        }

        var token = typeManager.readValue(secret, AzureSasToken.class);
        var folderName = dataAddress.getStringProperty(AzureBlobStoreSchema.FOLDER_NAME);
        var blobName = dataAddress.getStringProperty(AzureBlobStoreSchema.BLOB_NAME);
        var destinationBlobName = new DestinationBlobName(blobName, folderName);

        return SovityAzureStorageDataSink.Builder.newInstance()
                .accountName(dataAddress.getStringProperty(ACCOUNT_NAME))
                .containerName(dataAddress.getStringProperty(AzureBlobStoreSchema.CONTAINER_NAME))
                .destinationBlobName(destinationBlobName)
                .sharedAccessSignature(token.getSas())
                .requestId(requestId)
                .partitionSize(partitionSize)
                .blobStoreApi(blobStoreApi)
                .executorService(executorService)
                .monitor(monitor)
                .request(request)
                .metadataProvider(metadataProvider)
                .build();
    }

    @Override
    public @NotNull Result<Void> validateRequest(DataFlowStartMessage request) {
        var dataAddress = request.getDestinationDataAddress();
        var dataSourceAddress = request.getSourceDataAddress();

        try {
            validateAccountName(dataAddress.getStringProperty(ACCOUNT_NAME));
            validateContainerName(dataAddress.getStringProperty(CONTAINER_NAME));
            validateKeyName(dataAddress.getKeyName());
            if (dataSourceAddress.hasProperty(BLOB_PREFIX)) {
                if (dataSourceAddress.hasProperty(BLOB_NAME)) {
                    monitor.warning("Folder transfer (property '%s' is present), will ignore the blob name (property '%s')".formatted(BLOB_PREFIX, BLOB_NAME));
                }
            }
        } catch (IllegalArgumentException e) {
            return Result.failure("AzureStorage destination address is invalid: " + e.getMessage());
        }
        return Result.success();
    }
}
