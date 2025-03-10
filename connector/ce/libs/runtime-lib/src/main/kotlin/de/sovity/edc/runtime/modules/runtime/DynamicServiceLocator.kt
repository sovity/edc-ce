/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.runtime

import de.sovity.edc.runtime.modules.dependency_bundles.ServiceClassRegistry
import org.eclipse.edc.boot.system.ServiceLocator

/**
 * Implementation of an Eclipse EDC [ServiceLocator] that provides instances of service class interfaces.
 *
 * Does not use META-INF/services to find services but instead uses our in-memory [ServiceClassRegistry].
 *
 * Service classes are primarily Eclipse EDC Extensions. Careful though, they have different extension classes, e.g.
 * a SystemExtension is not the same as a ServiceExtension
 */
class DynamicServiceLocator(private val registry: ServiceClassRegistry) : ServiceLocator {
    override fun <T : Any> loadImplementors(type: Class<T>, required: Boolean): List<T> {
        val implementations = getImplementations(type)
        require(!required || implementations.isNotEmpty()) {
            "No classes found of type $type"
        }
        return implementations
    }

    override fun <T : Any> loadSingletonImplementor(type: Class<T>, required: Boolean): T {
        val implementations = getImplementations(type)
        require(!required || implementations.isNotEmpty()) {
            "No classes found of type $type"
        }
        require(implementations.size == 1) {
            "Multiple classes found of type $type: ${implementations.joinToString { it.javaClass.name }}"
        }
        return implementations.single()
    }

    private fun <T : Any> getImplementations(serviceClass: Class<T>): List<T> {
        return registry.getServiceClasses(serviceClass).map {
            it.getDeclaredConstructor().newInstance()
        }
    }
}
