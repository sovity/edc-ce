/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

/**
 * Only for Documentation Generation
 *
 * See [The module system](docs/dev/module_system.md#properties)
 */
enum class ConfigPropCategory(val documentationName: String, val order: Int) {
    IMPORTANT("Important Config", 0),
    OPTIONAL("Optional Config", 1),
    OVERRIDES("Overrides / Ignore", 2),
    UNKNOWN("Unknown", 9999)
}
