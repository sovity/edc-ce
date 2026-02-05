/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.azure_provision

import de.sovity.edc.runtime.simple_di.MigrationSensitive
import dev.failsafe.RetryPolicy
import org.eclipse.edc.azure.blob.AzureSasToken
import org.eclipse.edc.azure.blob.api.BlobStoreApi
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.TransferTypeParser
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ProvisionManager
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ResourceManifestGenerator
import org.eclipse.edc.connector.provision.azure.AzureProvisionConfiguration
import org.eclipse.edc.connector.provision.azure.AzureProvisionExtension
import org.eclipse.edc.runtime.metamodel.annotation.Configuration
import org.eclipse.edc.runtime.metamodel.annotation.Extension
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.types.TypeManager

@MigrationSensitive(
    changes = "Use fixed ObjectStorageResourceDefinition. " +
        "Will be fixed (and can be removed) as of tag Technology-Azure v0.8.1 / EDC 0.13.0",
    dependsOn = AzureProvisionExtension::class
)
@Extension(value = SovityAzureProvisionExtension.NAME)
class SovityAzureProvisionExtension : ServiceExtension {
    companion object {
        const val NAME: String = "Azure Provision Extension"
    }

    @Configuration
    private lateinit var azureProvisionConfiguration: AzureProvisionConfiguration

    @Inject
    private lateinit var blobStoreApi: BlobStoreApi

    @Inject
    private lateinit var retryPolicy: RetryPolicy<Any>

    @Inject
    private lateinit var manifestGenerator: ResourceManifestGenerator

    @Inject
    private lateinit var typeManager: TypeManager

    @Inject
    private lateinit var transferTypeParser: TransferTypeParser

    @Inject
    private lateinit var provisionManager: ProvisionManager

    override fun initialize(context: ServiceExtensionContext) {
        provisionManager.register(
            // Use Sovity Variant of ObjectStorageProvisioner
            SovityObjectStorageProvisioner(
                retryPolicy,
                context.monitor,
                blobStoreApi,
                azureProvisionConfiguration
            )
        )
        // Use Sovity Variant of ObjectStorageConsumerResourceDefinitionGenerator
        manifestGenerator.registerGenerator(SovityObjectStorageConsumerResourceDefinitionGenerator(transferTypeParser))

        registerTypes(typeManager)
    }

    private fun registerTypes(typeManager: TypeManager) {
        typeManager.registerTypes(
            // Use sovity Variant of ObjectContainerProvisionedResource
            SovityObjectContainerProvisionedResource::class.java,
            // Use Sovity Variant of ObjectStorageResourceDefinition
            SovityObjectStorageResourceDefinition::class.java,
            AzureSasToken::class.java
        )
    }
}
