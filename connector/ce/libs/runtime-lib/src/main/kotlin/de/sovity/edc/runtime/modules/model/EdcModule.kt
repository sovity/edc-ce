/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

import de.sovity.edc.runtime.modules.dependency_bundles.DependencyBundle
import de.sovity.edc.runtime.modules.dependency_bundles.ServiceClassRegistry
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.configuration.Config

/**
 * Tree-Like structure of modules.
 *
 * On start-up, given a config the config has to be evaluated to decide:
 *  - Which child modules are active, recursively, which implies which config properties are active
 *  - Are the active config properties valid?
 *  - What are the final config properties to start the EDC with?
 */
open class EdcModule(val name: String, val documentation: String) {
    private val kebabCase = Regex("^[a-z][a-z0-9]*(-[a-z][a-z0-9]*)*$")

    private val services = ServiceClassRegistry()
    private val props = mutableListOf<ConfigProp>()
    private val dockerImageEnvVars = mutableListOf<DocumentedDockerImageEnvVar>()
    private val childModules = mutableListOf<EdcConditionalModule>()
    private val vaultEntries = mutableListOf<VaultEntry>()

    init {
        require(kebabCase.matches(name)) { "Module name must be in kebabCase: $name" }
    }

    /**
     * Adds a property.
     *
     * Contents of the [configPropertyFinalizer] decide defaulting, the conditional nature, etc.
     */
    fun property(
        category: ConfigPropCategory,
        configProperty: ConfigPropRef,
        configPropertyFinalizer: ConfigProp.() -> Unit = {}
    ): EdcModule = apply {
        configProperty.toConfigProp(category)
            .apply(configPropertyFinalizer)
            .also { props.add(it) }
    }

    fun documentDockerImageEnvVar(
        envVarName: String,
        requiredOrDefault: String,
        documentation: String
    ): EdcModule = apply {
        dockerImageEnvVars.add(
            DocumentedDockerImageEnvVar(
                envVarName = envVarName,
                requiredOrDefault = requiredOrDefault,
                documentation = documentation
            )
        )
    }

    fun vaultEntry(vaultEntry: VaultEntry): EdcModule = apply {
        vaultEntries.add(vaultEntry)
    }

    /**
     * Adds child modules.
     */
    fun modules(vararg modules: EdcModule) = apply {
        modules.forEach { module(it) }
    }

    /**
     * Adds child module.
     */
    fun module(module: EdcModule): EdcModule = apply {
        EdcConditionalModule(
            condition = DocumentedFn("always") { true },
            module = module,
            documentation = module.documentation,
        ).also { childModules.add(it) }
    }

    /**
     * Adds child module if given property is true
     */
    fun moduleIfTrue(
        category: ConfigPropCategory,
        module: EdcModule,
        propRef: ConfigPropRef,
        finalizer: ConfigProp.() -> Unit = {}
    ): EdcModule = apply {
        // also register the deciding property
        property(category, propRef, finalizer)

        EdcConditionalModule(
            condition = DocumentedFn("`${propRef.property}`=`true`") {
                propRef.getBooleanOrFalse(it)
            },
            module = module,
            documentation = module.documentation,
        ).also { childModules.add(it) }
    }

    /**
     * Adds child module if given property is true
     */
    fun moduleIf(
        condition: DocumentedFn<Config, Boolean>,
        module: EdcModule,
        documentation: String
    ): EdcModule = apply {
        EdcConditionalModule(
            condition = condition,
            module = module,
            documentation = documentation
        ).also { childModules.add(it) }
    }

    /**
     * Adds one of the child modules, depending on the value of the given property
     */
    fun moduleOneOf(
        category: ConfigPropCategory,
        configProperty: ConfigPropRef,
        configPropertyFinalizer: ConfigProp.() -> Unit,
        vararg choices: EdcModule
    ): EdcModule = apply {
        require(choices.distinct().size == choices.size) {
            "Module choices must be unique: ${choices.joinToString { it.name }}"
        }
        configProperty.toConfigProp(category)
            .apply(configPropertyFinalizer)
            .apply {
                enumValues(choices.map { ConfigPropEnumValue(it.name, it.documentation) })
                validateFn = { value, _ ->
                    val valid = choices.any { it.name == value }
                    "Needs to be one of: ${choices.joinToString { "\"${it.name}\"" }}.".takeIf { !valid }
                }
            }
            .also { props.add(it) }
        choices.forEach { choice ->
            EdcConditionalModule(
                condition = DocumentedFn("`${configProperty.property}`=`${choice.name}`") {
                    configProperty.getStringOrNull(it) == choice.name
                },
                module = choice,
                documentation = choice.documentation
            ).also { childModules.add(it) }
        }
    }


    /**
     * Add all services from given service class bundle
     *
     * Service class bundles are aggregated from Eclipse EDC dependency bundles
     */
    fun dependencyBundle(bundle: DependencyBundle): EdcModule = apply {
        services.addDependencyBundle(bundle)
    }

    /**
     * Add all services from given service class bundles
     *
     * Service class bundles are aggregated from Eclipse EDC dependency bundles
     */
    fun dependencyBundles(vararg bundles: DependencyBundle): EdcModule = apply {
        bundles.forEach { services.addDependencyBundle(it) }
    }

    /**
     * Add service class implementation
     */
    fun <T : Any> serviceClass(serviceClass: Class<T>, implementationClass: Class<out T>): EdcModule = apply {
        services.addServiceClass(serviceClass, implementationClass)
    }

    /**
     * Add service class implementations for [ServiceExtension]
     */
    fun serviceExtensions(vararg extensions: Class<out ServiceExtension>): EdcModule = apply {
        extensions.forEach { services.addServiceClass(ServiceExtension::class.java, it) }
    }

    fun excludeServiceExtensions(vararg extensions: Class<out ServiceExtension>): EdcModule = apply {
        extensions.forEach { services.excludeServiceClass(ServiceExtension::class.java, it) }
    }

    /**
     * Excludes the given service class implementation
     */
    fun <T : Any> excludeServiceClass(serviceClass: Class<T>, implementationClass: Class<out T>): EdcModule = apply {
        services.excludeServiceClass(serviceClass, implementationClass)
    }

    fun getChildModules(): List<EdcConditionalModule> =
        childModules.toList()

    fun getConfigProps(): List<ConfigProp> =
        props.toList()

    fun getDockerImageEnvProps(): List<DocumentedDockerImageEnvVar> =
        dockerImageEnvVars.toList()

    fun getServices(): ServiceClassRegistry =
        services

    fun getVaultEntries(): List<VaultEntry> =
        vaultEntries.toList()

    fun getAllModulesRecursively(): List<EdcModule> =
        listOf(this) + childModules.flatMap { it.module.getAllModulesRecursively() }
}

