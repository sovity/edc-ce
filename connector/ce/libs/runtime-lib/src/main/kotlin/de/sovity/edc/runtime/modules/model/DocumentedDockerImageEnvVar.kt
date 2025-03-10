/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

/**
 * For non-EDC non-application logic configuration
 *
 * These are properties that aren't validated by the EDC
 */
class DocumentedDockerImageEnvVar(
    /**
     * The name of the environment variable
     */
    val envVarName: String,

    /**
     * Documentation in Markdown about the "required" state or "default value"
     */
    val requiredOrDefault: String,

    /**
     * General documentation in Markdown
     */
    val documentation: String
) {
    init {
        require(envVarName.matches(Regex("^[A-Z_][A-Z0-9_]*$"))) {
            "Docker Image documented Env variable must be in uppercase snake case: $envVarName"
        }
    }
}
