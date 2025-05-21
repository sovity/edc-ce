/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

import de.sovity.edc.extension.e2e.utils.Lazy

/**
 * Instance to be added to a JUnit test for parameter resolving
 *
 * (potentially lazy intialized)
 * (potentially hidden behind required annotations on the parameter)
 */
data class Instance<T>(
    val clazz: Class<T>,
    val value: Lazy<T>,

    /**
     * Hide this instance behind these qualifying annotations
     */
    val annotations: List<Class<out Annotation>>
)
