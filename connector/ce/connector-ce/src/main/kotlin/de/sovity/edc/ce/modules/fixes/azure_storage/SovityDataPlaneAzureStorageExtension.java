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
import dev.failsafe.RetryPolicy;
import org.eclipse.edc.azure.blob.api.BlobStoreApi;
import org.eclipse.edc.connector.dataplane.azure.storage.DataPlaneAzureStorageExtension;
import org.eclipse.edc.connector.dataplane.azure.storage.metadata.BlobMetadataProvider;
import org.eclipse.edc.connector.dataplane.azure.storage.metadata.BlobMetadataProviderImpl;
import org.eclipse.edc.connector.dataplane.azure.storage.metadata.CommonBlobMetadataDecorator;
import org.eclipse.edc.connector.dataplane.azure.storage.pipeline.AzureStorageDataSourceFactory;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataTransferExecutorServiceContainer;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

/**
 * Provides support for reading data from an Azure Storage Blob endpoint and sending data to an Azure Storage Blob endpoint.
 */
@MigrationSensitive(
    changes = "Use fixed AzureStorageDataSink, which will no longer create a completion marker file. " +
        "Will be fixed (and can be removed) as of EDC version 0.13.0",
    dependsOn = DataPlaneAzureStorageExtension.class
)
@Extension(value = SovityDataPlaneAzureStorageExtension.NAME)
public class SovityDataPlaneAzureStorageExtension implements ServiceExtension {

    public static final String NAME = "Data Plane Azure Storage";
    @Inject
    private RetryPolicy retryPolicy;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private BlobStoreApi blobStoreApi;

    @Inject
    private DataTransferExecutorServiceContainer executorContainer;

    @Inject
    private Vault vault;

    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        var metadataProvider = new BlobMetadataProviderImpl(monitor);
        context.registerService(BlobMetadataProvider.class, metadataProvider);
        metadataProvider.registerDecorator(new CommonBlobMetadataDecorator(typeManager, context));

        var sourceFactory = new AzureStorageDataSourceFactory(blobStoreApi, retryPolicy, monitor, vault);
        pipelineService.registerFactory(sourceFactory);
        var sinkFactory = new SovityAzureStorageDataSinkFactory(blobStoreApi, executorContainer.getExecutorService(), 5, monitor, vault, typeManager, metadataProvider);
        pipelineService.registerFactory(sinkFactory);
    }
}
