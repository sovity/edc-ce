/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.fixes.data_plane_framework

import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.connector.dataplane.framework.DataPlaneFrameworkExtension

object DataPlaneFrameworkOverrideModule {
    fun instance() = EdcModule(
        "dataplane-framework-override",
        "This override fixes a bug in the EDC 0.11.0, that is fixed in later versions:"
            + "https://github.com/eclipse-edc/Connector/pull/4982"
    ).apply {
        excludeServiceExtensions(DataPlaneFrameworkExtension::class.java)
        serviceExtensions(SovityDataPlaneFrameworkExtension::class.java)
    }
}
