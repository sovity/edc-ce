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


import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.sovity.edc.runtime.simple_di.MigrationSensitive;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.provision.azure.blob.ObjectStorageResourceDefinition;

import java.util.Objects;

@JsonTypeName("ObjectStorageResourceDefinition")
@JsonDeserialize(builder = SovityObjectStorageResourceDefinition.Builder.class)
@MigrationSensitive(changes = "Fixes serialization issue as in https://github.com/eclipse-edc/Technology-Azure/pull/339/files." +
    " Will be fixed as of v0.8.1-72-g250dd26",
    dependsOn = ObjectStorageResourceDefinition.class)
public class SovityObjectStorageResourceDefinition extends ResourceDefinition {

    private String containerName;
    private String accountName;
    private String folderName;
    private String blobName;
    private String keyName;

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
            .keyName(keyName)
            .accountName(accountName);
    }

    public String getFolderName() {
        return folderName;
    }

    public String getBlobName() {
        return blobName;
    }

    /**
     * Vault alias under which the storage account key is stored, carried from the sink DataAddress' keyName.
     * Required when the transfer is initiated via the UI API Wrapper (see {@code UiDataSinkAzureStorage#accountKey}).
     */
    public String getKeyName() {
        return keyName;
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

        public Builder keyName(String keyName) {
            resourceDefinition.keyName = keyName;
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
