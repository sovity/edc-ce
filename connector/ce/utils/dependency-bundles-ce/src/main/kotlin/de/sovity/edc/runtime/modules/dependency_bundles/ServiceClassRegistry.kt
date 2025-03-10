/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.dependency_bundles

/**
 * Merges [DependencyBundle]s and a [ServiceClassList]
 *
 * Service Classes in the meaning of the Java Service Locator API, where you can register implementation classes under
 * a service class for discovery by downstream applications in your JAR.
 *
 * See META-INF/services in EDC Extension JARs for example
 */
class ServiceClassRegistry() {
    /**
     * Take service classes from here
     */
    private val serviceClasses = ServiceClassList()

    /**
     * Also merge all service classes from these bundles
     */
    private val dependencyBundles = mutableListOf<DependencyBundle>()

    /**
     * Exclude those, probably because they have wrappers around them
     */
    private val exclusions = ServiceClassList()

    constructor(other: List<ServiceClassRegistry>) : this() {
        other.forEach { addAll(it) }
    }

    /**
     * Get service class implementations registered for the given service class,
     * e.g. [org.eclipse.edc.spi.system.ServiceExtension]
     */
    fun <T : Any> getServiceClasses(serviceClass: Class<T>): Set<Class<out T>> {
        val classes = serviceClasses.getServices(serviceClass).toMutableSet()
        classes.addAll(dependencyBundles.flatMap { bundle -> bundle.getServices(serviceClass) })
        classes.removeAll(exclusions.getServices(serviceClass))
        return classes
    }

    /**
     * Add all services from given service class bundle
     *
     * Service class bundles are aggregated from Eclipse EDC dependency bundles
     */
    fun addDependencyBundle(bundle: DependencyBundle) {
        dependencyBundles.add(bundle)
    }

    /**
     * Add service class implementation
     */
    fun <T : Any> addServiceClass(serviceClass: Class<T>, implementationClass: Class<out T>) {
        serviceClasses.addService(serviceClass, implementationClass)
    }

    /**
     * Exclude service class implementation
     */
    fun <T : Any> excludeServiceClass(serviceClass: Class<T>, implementationClass: Class<out T>) {
        exclusions.addService(serviceClass, implementationClass)
    }

    fun addAll(other: ServiceClassRegistry) {
        serviceClasses.addAll(other.serviceClasses)
        dependencyBundles.addAll(other.dependencyBundles)
        exclusions.addAll(other.exclusions)
    }

    /**
     * Allows us to print EDC dependencies on startup of active modules
     */
    fun getJars(): Set<String> =
        dependencyBundles.flatMap { it.jars }.toSet()
}
