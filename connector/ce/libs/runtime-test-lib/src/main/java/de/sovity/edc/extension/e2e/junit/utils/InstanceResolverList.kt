/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

class InstanceResolverList {
    private val instanceResolvers = mutableListOf<InstanceResolver>()

    fun add(resolver: InstanceResolver) {
        instanceResolvers.add(resolver)
    }

    fun has(clazz: Class<*>, annotations: List<Class<out Annotation>>): Boolean =
        instanceResolvers
            .any { annotations.containsAll(it.annotations) && it.has(clazz, annotations) }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>, annotations: List<Class<out Annotation>>): List<T> =
        instanceResolvers
            .filter { annotations.containsAll(it.annotations) && it.has(clazz, annotations) }
            .flatMap { it.get(clazz, annotations) as List<T> }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAllForCleanup(clazz: Class<T>): List<T> =
        instanceResolvers.flatMap { it.getAllForCleanup(clazz) as List<T> }
}
