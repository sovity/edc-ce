/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.azure_provision

import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.connector.provision.azure.AzureProvisionExtension

object AzureProvisionOverrideModule {
    fun instance() = EdcModule(
        "azure-provision-override",
        "This override fixes two bugs in the EDC 0.11.0, that are fixed in later versions:"
            + "1) Fix serialization of ObjectStorageResourceDefinition"
            + "2) Use blobName in SovityObjectContainerProvisionedResource"
    ).apply {
        excludeServiceExtensions(AzureProvisionExtension::class.java)
        serviceExtensions(SovityAzureProvisionExtension::class.java)
    }
}
