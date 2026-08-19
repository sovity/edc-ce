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

import com.azure.storage.blob.BlobServiceClient;
import de.sovity.edc.ce.api.utils.FieldAccessUtils;
import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import dev.failsafe.RetryPolicy;
import org.eclipse.edc.azure.blob.AzureSasToken;
import org.eclipse.edc.azure.blob.api.BlobStoreApi;
import org.eclipse.edc.azure.blob.api.BlobStoreApiImpl;
import org.eclipse.edc.azure.blob.cache.AccountCache;
import org.eclipse.edc.azure.blob.cache.AccountCacheImpl;
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
import org.eclipse.edc.spi.security.Vault;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static dev.failsafe.Failsafe.with;

@MigrationSensitive(changes = "Use fixed ObjectStorageResourceDefinition. Will be fixed as of v0.8.1-72-g250dd26. " +
    "Also makes the account key configurable via the DataAddress keyName instead of the hardcoded accountName + '-key1'." +
    "(https://github.com/sovity/edc-ee/issues/1622).",
    dependsOn = ObjectStorageProvisioner.class)
public class SovityObjectStorageProvisioner implements
    // Use sovity variant of ObjectStorageResourceDefinition
    Provisioner<SovityObjectStorageResourceDefinition, SovityObjectContainerProvisionedResource> {
    private final RetryPolicy<Object> retryPolicy;
    private final Monitor monitor;
    private final BlobStoreApi blobStoreApi;
    private final AzureProvisionConfiguration azureProvisionConfiguration;
    private final Vault vault;

    public SovityObjectStorageProvisioner(RetryPolicy<Object> retryPolicy, Monitor monitor, BlobStoreApi blobStoreApi, AzureProvisionConfiguration azureProvisionConfiguration, Vault vault) {
        this.retryPolicy = retryPolicy;
        this.monitor = monitor;
        this.blobStoreApi = blobStoreApi;
        this.azureProvisionConfiguration = azureProvisionConfiguration;
        this.vault = vault;
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
        String keyName = resourceDefinition.getKeyName();

        monitor.debug("Azure Storage Container request submitted: " + containerName);

        // Seed the account-key credential from the configurable vault alias so the subsequent
        // BlobStoreApi calls use it instead of the hardcoded accountName + "-key1" convention.
        seedAccountCredential(accountName, keyName);

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
                    .accountKeyName(keyName)
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
        // Seed the account-key credential from the configurable vault alias so the subsequent
        // BlobStoreApi calls use it instead of the hardcoded accountName + "-key1" convention.
        seedAccountCredential(provisionedResource.getAccountName(), provisionedResource.getAccountKeyName());
        return with(retryPolicy).runAsync(() -> blobStoreApi.deleteContainer(provisionedResource.getAccountName(), provisionedResource.getContainerName()))
            //the sas token will expire automatically. there is no way of revoking them other than a stored access policy
            .thenApply(empty -> StatusResult.success(DeprovisionedResource.Builder.newInstance().provisionedResourceId(provisionedResource.getId()).build()));
    }

    /**
     * <p>The {@link AccountCache}'s {@code getBlobServiceClient} used during provisioning hardcodes the account key to
     * the alias {@code accountName + "-key1"}. We exploit an implementation detail to avoid that: the two-argument
     * overload caches the built {@link BlobServiceClient} by account name, and the hardcoded overload reuses that
     * cached client whenever the account name is already present. So we call the two-argument overload once here with
     * the key resolved from the configurable {@code keyName}, seeding the cache before provisioning and deprovisioning.
     */
    private void seedAccountCredential(String accountName, String keyName) {
        if (keyName == null || keyName.isBlank()) {
            return;
        }
        var accountKey = vault.resolveSecret(keyName);
        if (accountKey == null) {
            monitor.warning("No secret found in vault under alias '%s' for storage account '%s'; falling back to '%s-key1'."
                .formatted(keyName, accountName, accountName));
            return;
        }
        accountCache().getBlobServiceClient(accountName, accountKey);
    }

    private AccountCache accountCache() {
        return FieldAccessUtils.accessField((BlobStoreApiImpl) blobStoreApi, "accountCache");
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
