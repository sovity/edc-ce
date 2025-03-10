/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.runtime.simple_di

import de.sovity.edc.runtime.simple_di.evaluation.EvaluationPath
import de.sovity.edc.runtime.simple_di.evaluation.Instantiator
import de.sovity.edc.runtime.simple_di.evaluation.MutableInstances
import kotlin.reflect.KClass

/**
 * Simple Dependency Injection System:
 * - Resolve implementations from other DI containers, e.g. EDC Services
 * - Resolve implementations from manually adding them
 * - Instantiates classes annotated with "@Service"
 * - Constructor Injection only
 *
 *
 * This is a utility you use explicitly in a given extension to avoid having to do tens or hundreds of constructor
 * calls. That's all it is. It is not an actual dependency injection container that would span an entire EDC.
 *
 * The idea is: You can give the EDC Services as 'additional source' with [addOtherDiContainer] to try to resolve
 * instances from, and you can also listen to the [onInstanceCreated] to put your created instances back into the
 * EDC again as services, so you can inject them in tests.
 *
 * See the README.md file for more information.
 */
class SimpleDi {
    private val instances = MutableInstances()
    private val otherDiContainers = mutableListOf<OtherDiContainer>()
    private val classes = mutableSetOf<Class<out Any>>()
    private val allowedPackages = mutableSetOf<String>()

    // Only used for detecting circular dependencies. See [instances] for the actual instances.
    private val visited = mutableSetOf<Class<out Any>>()

    private var onInstanceCreatedListeners = mutableListOf<(Any) -> Unit>()

    /**
     * Add class for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addClassToInstantiate(clazz: Class<out Any>) = addClassesToInstantiate(listOf(clazz))

    /**
     * Add classes for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addClassesToInstantiate(classes: Collection<Class<out Any>>) = this.apply {
        this.classes.addAll(classes)
    }

    /**
     * Add classes for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addClassesToInstantiate(vararg classes: Class<out Any>) = addClassesToInstantiate(classes.toList())

    /**
     * Add class for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addKClassToInstantiate(clazz: KClass<out Any>) = addClassesToInstantiate(listOf(clazz.java))

    /**
     * Add classes for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addKClassesToInstantiate(classes: Collection<KClass<out Any>>) =
        addClassesToInstantiate(classes.map { it.java })

    /**
     * Add classes for auto-instantiation. Must be annotated with [Service] and non-abstract.
     */
    fun addKClassesToInstantiate(vararg classes: KClass<out Any>) = addClassesToInstantiate(classes.map { it.java })

    /**
     * Add implementation. Useful for providing an implementation for an interface that is otherwise only referenced by
     * the interface
     */
    fun addInstance(instance: Any) = this.apply {
        instances.add(instance)
    }

    /**
     * Add implementations. Useful for providing implementations for interfaces otherwise only referenced by
     * the interfaces themselves
     */
    fun addInstances(instances: Collection<Any>) = this.apply {
        this.instances.addAll(instances)
    }

    /**
     * Add implementations. Useful for providing implementations for interfaces otherwise only referenced by
     * the interfaces themselves
     */
    fun addInstances(vararg instances: Any) = addInstances(instances.toList())

    /**
     * An allowlist to prevent accidental instantiation of instances that belong to other modules.
     * Use this to add the package of the module you're working with, the libraries and never add a package
     * that belongs to a different module.
     */
    fun addAllowedPackage(vararg packages: String): SimpleDi {
        allowedPackages.addAll(packages)
        return this
    }

    /**
     * Add another DI container to resolve instances via constructor parameters.
     */
    fun addOtherDiContainer(otherDiContainer: OtherDiContainer) = this.apply {
        otherDiContainers.add(otherDiContainer)
    }

    /**
     * Adds an event listener that is called for every [Service]-annotated instance created.
     *
     * This is useful for putting created instances back into another DI container
     */
    fun onInstanceCreated(onInstanceCreated: (Any) -> Unit) = this.apply {
        onInstanceCreatedListeners.add(onInstanceCreated)
    }

    /**
     * Instantiate all classes
     */
    fun toInstances(): Instances {
        classes.forEach {
            ensureInstantiated(it, EvaluationPath())
        }
        return instances
    }


    private fun ensureInstantiated(clazz: Class<out Any>, parentPath: EvaluationPath) {
        if (instances.has(clazz)) {
            return
        }

        // Check if another DI container can provide the instance
        val otherContainers = otherDiContainers.filter { it.has(clazz) }
        if (otherContainers.isNotEmpty()) {
            otherContainers.forEach { instances.add(it.getSingle(clazz)) }
            return
        }

        // Ensure class is instantiatable
        val currentPath = parentPath.forChild(clazz.name)
        instantiateClass(clazz, currentPath)
    }

    @Suppress("ThrowsCount")
    private fun instantiateClass(
        clazz: Class<out Any>,
        currentPath: EvaluationPath
    ) {
        if (!visited.add(clazz)) {
            throw SimpleDiException("Circular dependency detected: $currentPath")
        }

        requireInstantiatable(clazz, currentPath)

        if (allowedPackages.none { clazz.packageName.startsWith(it) }) {
            throw SimpleDiException(
                "Can't instantiate ${clazz.canonicalName} because it is not in an allowed package. Allowed packages are: ${
                    allowedPackages.joinToString(", ")
                }"
            )
        }

        // Recursion: On resolving any constructor parameter, potentially also instantiate that class
        val instanceWithInterceptor = instances.withAccessInterceptor {
            ensureInstantiated(it, parentPath = currentPath)
        }

        val value = try {
            Instantiator.instantiate(clazz, parameterResolver = { instanceWithInterceptor.getSingle(it) })
        } catch (e: SimpleDiException) {
            throw e
        } catch (e: Exception) {
            throw SimpleDiException(
                "$currentPath: Exception during instantiation: ${e.message ?: e.javaClass.name}",
                e
            )
        }

        // accept the new value
        instances.add(value)
        onInstanceCreatedListeners.forEach { it(value) }
    }

    private fun requireInstantiatable(
        clazz: Class<out Any>,
        currentPath: EvaluationPath
    ) {
        if (!Instantiator.isInstantiable(clazz)) {
            @Suppress("MaxLineLength")
            throw SimpleDiException("Class is abstract or an interface: $currentPath Add your implementation manually or add an 'OtherDiContainer' to let the DI find the implementation")
        }
        if (!clazz.isAnnotationPresent(Service::class.java)) {
            throw SimpleDiException("Class is missing @Service and was not a part of otherDiContainers: $currentPath")
        }
    }

    class SimpleDiException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}
