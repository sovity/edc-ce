/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation

import de.sovity.edc.runtime.modules.dependency_bundles.ServiceClassRegistry
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.spi.system.configuration.Config

class EdcModuleSystemResult(
    val activeModules: List<EdcModule>,
    val evaluatedConfig: Config
) {
    val serviceClasses = ServiceClassRegistry(activeModules.map { it.getServices() })
}
