/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.simple_di

import org.eclipse.edc.spi.system.ServiceExtensionContext

object SimpleDiExt {
    @Suppress("UNCHECKED_CAST")
    fun SimpleDi.onInstanceCreatedRegisterEdcService(
        context: ServiceExtensionContext
    ) = onInstanceCreated { instance ->
        context.registerService(instance.javaClass, instance)

        instance.javaClass.interfaces.forEach { interfaceClazz ->
            context.registerService(interfaceClazz as Class<Any>, instance)
        }
    }
}
