/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import org.eclipse.edc.spi.system.configuration.Config
import org.eclipse.edc.spi.system.configuration.ConfigFactory
import java.util.stream.Stream

/**
 * A mutable [Config] where you can intercept any read operation.
 *
 * We will use this to populate our properties step by step resolving dependencies between config properties on startup.
 */
class MutableInterceptableConfig(
    private val mutableConfig: MutableMap<String, String>,
    private val onBeforeAccessProperty: (key: String) -> Unit
) : Config {
    private val backingConfigForParsing = ConfigFactory.fromMap(mutableConfig)

    companion object {
        /**
         * Makes an immutable config mutable by creating a new [MutableInterceptableConfig]
         */
        fun root(config: Config) =
            MutableInterceptableConfig(config.entries.toMutableMap()) {}

        fun root() =
            MutableInterceptableConfig(mutableMapOf()) {}
    }

    /**
     * Returns a new [MutableInterceptableConfig] with the given interceptor.
     *
     * The mutable map is shared between all instances, however.
     *
     * You need this confusing mechanism so you can give code "a config", but react differently when a property is
     * accessed, e.g. by raising an exception showing the path on how you got there
     */
    fun withInterceptor(onBeforeAccessProperty: (key: String) -> Unit) =
        MutableInterceptableConfig(mutableConfig, onBeforeAccessProperty)

    /**
     * Build a normal [Config] from the current state of the mutable map.
     *
     * The created config now supports all operations again
     */
    fun toImmutableConfig(): Config =
        ConfigFactory.fromMap(mutableConfig.toMap())

    fun set(key: String, value: String) {
        mutableConfig[key] = value
    }

    override fun getString(key: String): String {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getString(key)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getString(key, defaultValue)
    }

    override fun getInteger(key: String): Int {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getInteger(key)
    }

    override fun getInteger(key: String, defaultValue: Int?): Int? {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getInteger(key, defaultValue)
    }

    override fun getLong(key: String): Long {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getLong(key)
    }

    override fun getLong(key: String, defaultValue: Long?): Long? {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getLong(key, defaultValue)
    }

    override fun getBoolean(key: String): Boolean {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getBoolean(key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean?): Boolean? {
        onBeforeAccessProperty(key)
        return backingConfigForParsing.getBoolean(key, defaultValue)
    }

    override fun getEntries(): MutableMap<String, String> =
        mutableConfig

    override fun getConfig(path: String?): Config {
        unsupported()
    }

    override fun merge(other: Config): Config {
        unsupported()
    }

    override fun partition(): Stream<Config> {
        unsupported()
    }

    override fun getRelativeEntries(): MutableMap<String, String> {
        unsupported()
    }

    override fun getRelativeEntries(basePath: String?): MutableMap<String, String> {
        unsupported()
    }

    override fun currentNode(): String {
        unsupported()
    }

    override fun isLeaf(): Boolean {
        unsupported()
    }

    override fun hasKey(key: String): Boolean {
        unsupported()
    }

    override fun hasPath(path: String?): Boolean {
        unsupported()
    }

    private fun unsupported(): Nothing {
        error("Unsupported method for ${this::class.simpleName}")
    }
}
