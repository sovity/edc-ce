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

    // Stored as a distinct property because the DataAddress' native keyName is already used for the
    // provisioned SAS-token secret name (see Builder#resourceName).
    private static final String ACCOUNT_KEY_NAME = "accountKeyName";

    private SovityObjectContainerProvisionedResource() {
    }

    public String getAccountName() {
        return getDataAddress().getStringProperty(ACCOUNT_NAME);
    }

    public String getContainerName() {
        return getDataAddress().getStringProperty(CONTAINER_NAME);
    }

    /**
     * Vault alias for the storage account key, needed to authenticate against the account on deprovision.
     * Carried over from the provisioned resource definition's keyName.
     */
    public String getAccountKeyName() {
        return getDataAddress().getStringProperty(EDC_NAMESPACE + ACCOUNT_KEY_NAME);
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

        // Vault alias for the storage account key, carried through so deprovision can authenticate
        public Builder accountKeyName(String keyName) {
            if (keyName != null) {
                dataAddressBuilder.property(EDC_NAMESPACE + ACCOUNT_KEY_NAME, keyName);
            }
            return this;
        }
    }
}
