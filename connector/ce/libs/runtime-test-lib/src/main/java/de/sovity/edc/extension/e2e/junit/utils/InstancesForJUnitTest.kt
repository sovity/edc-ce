/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

import de.sovity.edc.extension.e2e.utils.Lazy
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * Manage instances to be resolved either by the built-in [ParameterResolver] or for stacking types of tests,
 * e.g. making an EDC Integration test into a 2x EDC Integration test
 *
 * Annotations are used as qualifiers. The logic is like this:
 * - If an instance is registered with an annotation, you need to have those annotations on when you try to resolve it
 * - If you don't have any annotations on your field to resolve, only those instances without annotations will be returned
 * - The method getAllForCleanup will return all instances, regardless of annotations
 */
class InstancesForJUnitTest : ParameterResolver {
    private val instances = InstanceList()
    private val childResolvers = InstanceResolverList()

    fun addInstance(
        instance: Any,
        annotations: List<Class<out Annotation>> = listOf()
    ) {
        instances.add(
            Instance(
                instance.javaClass,
                Lazy.ofConstant(instance),
                annotations
            )
        )
    }

    fun removeInstance(clazz: Class<*>) {
        instances.remove(clazz)
    }

    fun <T> addInstanceLazy(
        clazz: Class<T>,
        factory: () -> T,
        annotations: List<Class<out Annotation>> = listOf()
    ) {
        instances.add(
            Instance(
                clazz,
                Lazy.ofLazy(factory),
                annotations
            )
        )
    }

    fun addInstanceResolver(instanceResolver: InstanceResolver) {
        childResolvers.add(instanceResolver)
    }

    /**
     * Add all instances and resolvers from another [InstancesForJUnitTest] to this one,
     * potentially further restricting access behind additional qualifying annotations
     */
    fun addAll(other: InstancesForJUnitTest, annotations: List<Class<out Annotation>> = listOf()) {
        addInstanceResolver(
            InstanceResolver(
                get = { clazz, annotations2 -> other.get(clazz, *annotations2.toTypedArray()) },
                has = other::has,
                // this will add the additional layer of required annotations
                annotations = annotations
            )
        )
    }

    fun has(clazz: Class<*>, annotations: List<Class<out Annotation>> = listOf()): Boolean =
        instances.has(clazz, annotations) || childResolvers.has(clazz, annotations)

    fun <T> getSingle(clazz: Class<T>, vararg annotations: Class<out Annotation>): T {
        return get(clazz, *annotations).singleOrNull()
            ?: error(
                "Expected exactly one instance for type $clazz with given annotations ${annotations.map { it.name }}, " +
                    "but got ${get(clazz, *annotations)}"
            )
    }

    fun <T> get(clazz: Class<T>, vararg annotations: Class<out Annotation>): List<T> {
        val result = mutableListOf<T>()

        val annotationsList = annotations.toList()
        if (instances.has(clazz, annotationsList)) {
            result += instances.get(clazz, annotationsList)
        }

        if (childResolvers.has(clazz, annotationsList)) {
            result += childResolvers.get(clazz, annotationsList)
        }

        result.ifEmpty {
            error("No instance found for type $clazz with given annotations: ${annotations.map { it.name }}")
        }

        return result
    }

    fun <T> getAllForCleanup(clazz: Class<T>): List<T> =
        instances.getAllForCleanup(clazz) + childResolvers.getAllForCleanup(clazz)

    @Throws(ParameterResolutionException::class)
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean {
        val clazz = parameterContext.parameter.type
        val annotations = parameterContext.parameter.annotations.map { it.annotationClass.java }
        return has(clazz, annotations)
    }

    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any {
        val clazz = parameterContext.parameter.type
        val annotations = parameterContext.parameter.annotations
            .map { it.annotationClass.java }
            .toTypedArray()
        return getSingle(clazz, *annotations)
    }
}
