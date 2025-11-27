/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.blob_storage

import com.azure.storage.blob.models.BlobContainerItem
import com.azure.storage.blob.models.BlobItem
import de.sovity.edc.ce.api.utils.FieldAccessUtils
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.azure.blob.api.BlobStoreApi
import org.eclipse.edc.azure.blob.api.BlobStoreApiImpl
import org.eclipse.edc.azure.blob.cache.AccountCache
import org.eclipse.edc.spi.EdcException
import org.eclipse.edc.spi.security.Vault

@Service
class SovityBlobStoreApiImpl(
    private val vault: Vault,
    private val blobStoreApi: BlobStoreApi
) {

    private val accountCache: AccountCache by lazy {
        val blobStoreApiImpl = blobStoreApi as BlobStoreApiImpl
        FieldAccessUtils.accessField(blobStoreApiImpl, "accountCache")
    }

    fun listAllContainers(accountKey: String, accountName: String): List<BlobContainerItem> {
        val secret =
            vault.resolveSecret(accountKey) ?: throw EdcException("Could not resolve secret for key $accountKey")
        val client = accountCache.getBlobServiceClient(
            accountName, secret
        )
        return client.listBlobContainers().toList()
    }

    fun listContainer(accountKey: String, accountName: String, containerName: String): MutableList<BlobItem> {
        accountCache.getBlobServiceClient(accountName, vault.resolveSecret(accountKey))
        return blobStoreApi.listContainer(accountName, containerName)
    }
}
