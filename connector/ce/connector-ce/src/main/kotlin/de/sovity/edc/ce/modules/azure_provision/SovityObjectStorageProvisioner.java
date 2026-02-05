/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.azure_provision;

import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import dev.failsafe.RetryPolicy;
import org.eclipse.edc.azure.blob.AzureSasToken;
import org.eclipse.edc.azure.blob.api.BlobStoreApi;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.Provisioner;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.DeprovisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionResponse;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.provision.azure.AzureProvisionConfiguration;
import org.eclipse.edc.connector.provision.azure.blob.ObjectStorageProvisioner;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.response.StatusResult;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static dev.failsafe.Failsafe.with;

@MigrationSensitive(changes = "Use fixed ObjectStorageResourceDefinition. Will be fixed as of v0.8.1-72-g250dd26",
    dependsOn = ObjectStorageProvisioner.class)
public class SovityObjectStorageProvisioner implements
    // Use sovity variant of ObjectStorageResourceDefinition
    Provisioner<SovityObjectStorageResourceDefinition, SovityObjectContainerProvisionedResource> {
    private final RetryPolicy<Object> retryPolicy;
    private final Monitor monitor;
    private final BlobStoreApi blobStoreApi;
    private final AzureProvisionConfiguration azureProvisionConfiguration;

    public SovityObjectStorageProvisioner(RetryPolicy<Object> retryPolicy, Monitor monitor, BlobStoreApi blobStoreApi, AzureProvisionConfiguration azureProvisionConfiguration) {
        this.retryPolicy = retryPolicy;
        this.monitor = monitor;
        this.blobStoreApi = blobStoreApi;
        this.azureProvisionConfiguration = azureProvisionConfiguration;
    }

    @Override
    public boolean canProvision(ResourceDefinition resourceDefinition) {
        // Use sovity variant of ObjectStorageResourceDefinition
        return resourceDefinition instanceof SovityObjectStorageResourceDefinition;
    }

    @Override
    public boolean canDeprovision(ProvisionedResource resourceDefinition) {
        // Use sovity variant of ObjectContainerProvisionedResource
        return resourceDefinition instanceof SovityObjectContainerProvisionedResource;
    }

    @Override
    public CompletableFuture<StatusResult<ProvisionResponse>> provision(SovityObjectStorageResourceDefinition resourceDefinition, Policy policy) {
        String containerName = resourceDefinition.getContainerName();
        String accountName = resourceDefinition.getAccountName();
        String folderName = resourceDefinition.getFolderName();
        String blobName = resourceDefinition.getBlobName();

        monitor.debug("Azure Storage Container request submitted: " + containerName);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(this.azureProvisionConfiguration.tokenExpiryTime());

        return with(retryPolicy).getAsync(() -> blobStoreApi.exists(accountName, containerName))
            .thenCompose(exists -> {
                if (exists) {
                    return reusingExistingContainer(containerName);
                } else {
                    return createContainer(containerName, accountName);
                }
            })
            .thenCompose(empty -> createContainerSasToken(containerName, accountName, expiryTime))
            .thenApply(writeOnlySas -> {
                // Ensure resource name is unique to avoid key collisions in local and remote vaults
                String resourceName = resourceDefinition.getId() + "-container";
                // Use sovity variant of SovityObjectContainerProvisionedResource
                var resource = SovityObjectContainerProvisionedResource.Builder.newInstance()
                    .id(containerName)
                    .accountName(accountName)
                    .containerName(containerName)
                    .folderName(folderName)
                    .blobName(blobName)
                    .resourceDefinitionId(resourceDefinition.getId())
                    .transferProcessId(resourceDefinition.getTransferProcessId())
                    .resourceName(resourceName)
                    .hasToken(true)
                    .build();

                var secretToken = new AzureSasToken("?" + writeOnlySas, expiryTime.toInstant().toEpochMilli());

                var response = ProvisionResponse.Builder.newInstance().resource(resource).secretToken(secretToken).build();
                return StatusResult.success(response);
            });
    }

    @Override
    public CompletableFuture<StatusResult<DeprovisionedResource>> deprovision(SovityObjectContainerProvisionedResource provisionedResource, Policy policy) {
        return with(retryPolicy).runAsync(() -> blobStoreApi.deleteContainer(provisionedResource.getAccountName(), provisionedResource.getContainerName()))
            //the sas token will expire automatically. there is no way of revoking them other than a stored access policy
            .thenApply(empty -> StatusResult.success(DeprovisionedResource.Builder.newInstance().provisionedResourceId(provisionedResource.getId()).build()));
    }

    @NotNull
    private CompletableFuture<Void> reusingExistingContainer(String containerName) {
        monitor.debug("ObjectStorageProvisioner: re-use existing container " + containerName);
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    private CompletableFuture<Void> createContainer(String containerName, String accountName) {
        return with(retryPolicy)
            .runAsync(() -> {
                blobStoreApi.createContainer(accountName, containerName);
                monitor.debug("ObjectStorageProvisioner: created a new container " + containerName);
            });
    }

    @NotNull
    private CompletableFuture<String> createContainerSasToken(String containerName, String accountName, OffsetDateTime expiryTime) {
        return with(retryPolicy)
            .getAsync(() -> {
                monitor.debug("ObjectStorageProvisioner: obtained temporary SAS token (write-only)");
                return blobStoreApi.createContainerSasToken(accountName, containerName, "w", expiryTime);
            });
    }
}
