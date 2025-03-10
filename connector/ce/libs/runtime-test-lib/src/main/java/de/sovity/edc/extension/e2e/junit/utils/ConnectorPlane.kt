/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

import org.junit.jupiter.api.extension.ParameterContext

enum class ConnectorPlane {
    CONTROL_PLANE,
    DATA_PLANE;

    companion object {
        fun fromParameterContextOrNull(parameterContext: ParameterContext): ConnectorPlane? {
            val isControlPlane = parameterContext.parameter.getDeclaredAnnotation(ControlPlane::class.java) != null
            val isDataPlane = parameterContext.parameter.getDeclaredAnnotation(DataPlane::class.java) != null

            return when {
                isDataPlane && !isControlPlane -> DATA_PLANE
                !isDataPlane && isControlPlane -> CONTROL_PLANE
                isDataPlane && isControlPlane -> error("Parameter cannot be annotated with both ControlPlane and DataPlane")
                else -> null
            }
        }
    }
}
