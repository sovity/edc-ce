/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

/**
 * Documented expression / function
 *
 * Used to ensure each requiredFn or defaultValueFn has a human readable
 * documentation string
 */
data class DocumentedFn<A, B>(
    /**
     * Documentation in Markdown
     *
     * Required to explain what the function does in our generated docs
     */
    val documentation: String,

    /**
     * The function
     */
    val fn: (a: A) -> B
)
