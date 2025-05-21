/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.utils

class Lazy<T> private constructor(
    private val supplier: (() -> T)? = null,
    private var instance: T? = null
) {

    @Synchronized
    fun get(): T {
        if (instance == null) {
            instance = supplier?.invoke()
        }
        return instance!!
    }

    val isInitialized: Boolean
        @Synchronized
        get() = instance != null

    companion object {
        fun <T> ofLazy(supplier: () -> T): Lazy<T> =
            Lazy(supplier = supplier)

        fun <T> ofConstant(value: T): Lazy<T> {
            require(value != null) { "'Lazy' does not support null values" }
            return Lazy(instance = value)
        }
    }
}
