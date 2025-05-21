/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object PostgresTestcontainerModule {
    fun instance() = EdcModule(
        name = "postgres-testcontainer",
        documentation = "Launches DB via Testcontainers"
    ).apply {
        serviceExtensions(PostgresTestcontainerExtension::class.java)
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INITDB_ARGS,
        )
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INIT_SCRIPT,
        )
    }
}
