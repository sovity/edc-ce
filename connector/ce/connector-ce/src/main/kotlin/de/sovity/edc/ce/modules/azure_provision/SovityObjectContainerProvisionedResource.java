/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.azure_provision;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import org.eclipse.edc.azure.blob.AzureBlobStoreSchema;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedDataDestinationResource;
import org.eclipse.edc.connector.provision.azure.blob.ObjectContainerProvisionedResource;

import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.ACCOUNT_NAME;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.BLOB_NAME;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.CONTAINER_NAME;
import static org.eclipse.edc.azure.blob.AzureBlobStoreSchema.FOLDER_NAME;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

@MigrationSensitive(dependsOn = ObjectContainerProvisionedResource.class,
    changes = "Added blobName property as fixed in https://github.com/eclipse-edc/Technology-Azure/pull/344." +
        "This will be fixed as of v0.8.1-76-g30135b8")
@JsonDeserialize(builder = SovityObjectContainerProvisionedResource.Builder.class)
@JsonTypeName("dataspaceconnector:ObjectContainerProvisionedResource")
public class SovityObjectContainerProvisionedResource extends ProvisionedDataDestinationResource {

    private SovityObjectContainerProvisionedResource() {
    }

    public String getAccountName() {
        return getDataAddress().getStringProperty(ACCOUNT_NAME);
    }

    public String getContainerName() {
        return getDataAddress().getStringProperty(CONTAINER_NAME);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends ProvisionedDataDestinationResource.Builder<SovityObjectContainerProvisionedResource, Builder> {

        private Builder() {
            super(new SovityObjectContainerProvisionedResource());
            dataAddressBuilder.type(AzureBlobStoreSchema.TYPE);
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder accountName(String accountName) {
            dataAddressBuilder.property(EDC_NAMESPACE + ACCOUNT_NAME, accountName);
            return this;
        }

        public Builder containerName(String containerName) {
            dataAddressBuilder.property(EDC_NAMESPACE + CONTAINER_NAME, containerName);
            return this;
        }

        @Override
        public Builder resourceName(String name) {
            dataAddressBuilder.keyName(name);
            super.resourceName(name);
            return this;
        }

        public Builder folderName(String folderName) {
            if (folderName != null) {
                dataAddressBuilder.property(EDC_NAMESPACE + FOLDER_NAME, folderName);
            }
            return this;
        }

        // New property blobName
        public Builder blobName(String blobName) {
            if (blobName != null) {
                dataAddressBuilder.property(EDC_NAMESPACE + BLOB_NAME, blobName);
            }
            return this;
        }
    }
}
