/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.test_utils

import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsModule
import de.sovity.edc.runtime.modules.model.EdcModule

object TestBackendModule {
    fun instance() = EdcModule(
        name = "e2e-test-backend",
        documentation = "EDC Test Backend"
    ).apply {
        dependencyBundle(CeDependencyBundles.testBackend)
        modules(ConfigUtilsModule.instance())
        serviceExtensions(TestBackendExtension::class.java)
    }
}
