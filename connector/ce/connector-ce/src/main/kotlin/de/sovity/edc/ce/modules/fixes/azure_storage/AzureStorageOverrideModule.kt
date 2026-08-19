/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.fixes.azure_storage

import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.connector.dataplane.azure.storage.DataPlaneAzureStorageExtension

object AzureStorageOverrideModule {
    fun instance() = EdcModule(
        "azure-storage-override",
        "This override fixes a bug in the EDC 0.11.0, where for the Azure Blob transfer a completion marker file is " +
            "unnecessarily created to indicate that the transfer is complete. This is already fixed in later versions" +
            "as of Core-EDC 0.13.0 in the following PR: https://github.com/eclipse-edc/Technology-Azure/pull/345"
    ).apply {
        excludeServiceExtensions(DataPlaneAzureStorageExtension::class.java)
        serviceExtensions(SovityDataPlaneAzureStorageExtension::class.java)
    }
}
