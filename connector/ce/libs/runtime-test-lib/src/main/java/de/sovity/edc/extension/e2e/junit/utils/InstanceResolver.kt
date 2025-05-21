/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

/**
 * Instance resolver to be delegated to for JUnit parameter resolving
 *
 * (potentially hidden behind required annotations on the parameter)
 */
data class InstanceResolver(
    val has: (clazz: Class<*>, annotations: List<Class<out Annotation>>) -> Boolean,
    val get: (clazz: Class<*>, annotations: List<Class<out Annotation>>) -> List<Any>,

    /**
     * This gets called without a prior "has" call
     */
    val getAllForCleanup: (clazz: Class<*>) -> List<Any> = { listOf() },

    /**
     * Hide this child resolver behind these qualifying annotations
     */
    val annotations: List<Class<out Annotation>>
)
