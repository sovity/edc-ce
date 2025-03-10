/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

import org.eclipse.edc.spi.system.configuration.Config

class EdcConditionalModule(
    val condition: DocumentedFn<Config, Boolean>,
    val module: EdcModule,

    /**
     * Might differ from the module documentation in cases where we use a [DocumentedEnum]-backed config property
     * that activates multiple modules, e.g. sovity.dataspace.kind=catena-x
     */
    val documentation: String,
)
