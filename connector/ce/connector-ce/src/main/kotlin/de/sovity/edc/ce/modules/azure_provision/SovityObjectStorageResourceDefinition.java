/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.azure_provision;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.provision.azure.blob.ObjectStorageResourceDefinition;

import java.util.Objects;

@JsonDeserialize(builder = SovityObjectStorageResourceDefinition.Builder.class)
@MigrationSensitive(changes = "Fixes serialization issue as in https://github.com/eclipse-edc/Technology-Azure/pull/339/files." +
    " Will be fixed as of v0.8.1-72-g250dd26",
    dependsOn = ObjectStorageResourceDefinition.class)
public class SovityObjectStorageResourceDefinition extends ResourceDefinition {

    private String containerName;
    private String accountName;
    private String folderName;
    private String blobName;

    public String getContainerName() {
        return containerName;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public Builder toBuilder() {
        return initializeBuilder(new Builder())
            .containerName(containerName)
            .folderName(folderName)
            .blobName(blobName)
            .accountName(accountName);
    }

    public String getFolderName() {
        return folderName;
    }

    public String getBlobName() {
        return blobName;
    }

    // Added this as in https://github.com/eclipse-edc/Technology-Azure/pull/339/files
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends
        // Extend sovity variant of ObjectStorageResourceDefinition
        ResourceDefinition.Builder<SovityObjectStorageResourceDefinition, Builder> {

        // Use sovity variant of ObjectStorageResourceDefinition
        private Builder() {
            super(new SovityObjectStorageResourceDefinition());
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder containerName(String id) {
            resourceDefinition.containerName = id;
            return this;
        }

        public Builder accountName(String accountName) {
            resourceDefinition.accountName = accountName;
            return this;
        }

        public Builder folderName(String folderName) {
            resourceDefinition.folderName = folderName;
            return this;
        }

        public Builder blobName(String blobName) {
            resourceDefinition.blobName = blobName;
            return this;
        }

        @Override
        protected void verify() {
            super.verify();
            Objects.requireNonNull(resourceDefinition.containerName, "containerName");
            Objects.requireNonNull(resourceDefinition.accountName, "accountName");
        }
    }

}
