/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

import org.junit.jupiter.api.extension.ParameterContext

enum class E2eTestSide {
    PROVIDER,
    CONSUMER;

    companion object {
        fun fromParameterContextOrNull(parameterContext: ParameterContext): E2eTestSide? {
            val isProvider = parameterContext.parameter.getDeclaredAnnotation(Provider::class.java) != null
            val isConsumer = parameterContext.parameter.getDeclaredAnnotation(Consumer::class.java) != null

            return when {
                isConsumer && !isProvider -> CONSUMER
                !isConsumer && isProvider -> PROVIDER
                isConsumer && isProvider -> error("Parameter cannot be annotated with both Consumer and Provider")
                else -> null
            }
        }
    }
}
