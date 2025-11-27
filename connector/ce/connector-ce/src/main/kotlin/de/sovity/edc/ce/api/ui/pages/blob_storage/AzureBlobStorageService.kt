/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.blob_storage

import de.sovity.edc.ce.api.ui.model.AzureStorageListBlobsRequest
import de.sovity.edc.ce.api.ui.model.AzureStorageListContainersRequest
import de.sovity.edc.runtime.simple_di.Service

@Service
class AzureBlobStorageService(
    private val blobStoreApi: SovityBlobStoreApiImpl,
) {

    fun listContainerNames(request: AzureStorageListContainersRequest): List<String> =
        blobStoreApi.listAllContainers(
            accountKey = request.storageAccountVaultKey,
            accountName = request.storageAccountName
        ).map { it.name }

    fun listBlobNames(request: AzureStorageListBlobsRequest): List<String> =
        blobStoreApi.listContainer(
            accountKey = request.storageAccountVaultKey,
            accountName = request.storageAccountName,
            containerName = request.containerName
        ).map { it.name }
}
