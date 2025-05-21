/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.runtime.simple_di.SimpleDi
import de.sovity.edc.runtime.simple_di.SimpleDiExt.onInstanceCreatedRegisterEdcService
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.edr.spi.store.EndpointDataReferenceStore
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.transform.spi.TypeTransformerRegistry
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry

/**
 * Validates the time of negotiation / transfer initiation
 */
@Provides(EdrApiService::class)
class EdrApiServiceV3GenericExtension : ServiceExtension {
    @Inject
    lateinit var endpointDataReferenceStore: EndpointDataReferenceStore

    @Inject
    lateinit var typeTransformerRegistry: TypeTransformerRegistry

    @Inject
    lateinit var jsonObjectValidatorRegistry: JsonObjectValidatorRegistry

    @Inject
    lateinit var transferProcessService: TransferProcessService

    override fun initialize(context: ServiceExtensionContext) {
        SimpleDi()
            .addAllowedPackage(javaClass.packageName)
            .addClassesToInstantiate(
                EdrApiServiceV3GenericImpl::class.java
            )
            .addInstances(
                context.config,
                context.monitor,
                endpointDataReferenceStore,
                jsonObjectValidatorRegistry,
                typeTransformerRegistry,
                transferProcessService
            )
            .onInstanceCreatedRegisterEdcService(context)
            .toInstances()
    }
}
