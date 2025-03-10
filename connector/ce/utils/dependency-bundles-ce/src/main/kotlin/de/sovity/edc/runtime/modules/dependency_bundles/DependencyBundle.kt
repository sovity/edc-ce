/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.dependency_bundles

import lombok.RequiredArgsConstructor
import lombok.SneakyThrows
import java.nio.charset.StandardCharsets

/**
 * Service classes read from a dependency bundle.
 *
 * The Java ServiceLocator API uses contents from "META-INF/services" from the entire classpath.
 * This means if you add a jar, all service classes get loaded automatically.
 *
 * We don't want this, but we want to choose at runtime which extensions we want to activate.
 *
 * But since we also don't want to have to guess all classes of a dependency tree, we collect all services of a
 * dependency bundle at build time and make them available through instances of this class.
 */
@RequiredArgsConstructor
class DependencyBundle(
    val name: String,
    val documentation: String,

    /**
     * Allows us to print EDC dependencies on startup of active modules
     */
    val jars: Set<String>,

    /**
     * Base directory for the extracted service files in the class path.
     *
     * This is variable to prevent collisions between our CE and EE
     */
    private val serviceFilesDir: String,
) {
    /**
     * Get all implementations of a given class in this bundle.
     *
     * @param serviceClass service class interface name
     * @param <T> service class interface type
     * @return list of classes that implement the service class in this bundle
    </T> */
    fun <T> getServices(serviceClass: Class<T>): List<Class<out T>> {
        val classNames = readServiceClassFileContents(serviceClass.name)
        return loadClasses(serviceClass, classNames)
    }

    @SneakyThrows
    @Suppress("UNCHECKED_CAST")
    private fun <T> loadClasses(serviceClass: Class<T>, classNames: List<String>): List<Class<out T>> {
        return classNames.map { className ->
            val implementationClass = Class.forName(className)
            if (!serviceClass.isAssignableFrom(implementationClass)) {
                throw ClassCastException("Class $implementationClass does not implement $serviceClass")
            }
            implementationClass as Class<out T>
        }
    }

    private fun readServiceClassFileContents(file: String): List<String> {
        val filePath = "/$serviceFilesDir/$name/$file"
        val fileContents = readClasspathResourceOrBlank(filePath)
        return fileContents.split("\n")
            // remove comments
            .map { it.substringBefore("#").trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .toList()
    }

    @SneakyThrows
    private fun readClasspathResourceOrBlank(file: String): String {
        val fileResource = javaClass.getResource(file) ?: return ""
        return fileResource.readText(charset = StandardCharsets.UTF_8)
    }
}
