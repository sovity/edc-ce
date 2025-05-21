/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.db.PostgresTestcontainerStarter.startPostgresqlTestcontainer
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

@Provides(PostgresTestcontainer::class)
class PostgresTestcontainerExtension : ServiceExtension {
    private lateinit var container: PostgresTestcontainer

    override fun initialize(context: ServiceExtensionContext) {
        val monitor = context.monitor

        monitor.info("Starting PostgreSQL Testcontainer")
        container = startPostgresqlTestcontainer(
            username = "edc",
            password = "edc",
            databaseName = "edc",
            initDbArgs = CeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INITDB_ARGS.getStringOrNull(context.config),
            initScript = CeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INIT_SCRIPT.getStringOrNull(context.config),
        )
        monitor.info("PostgreSQL Testcontainer started!")

        context.registerService(
            PostgresTestcontainer::class.java,
            container
        )
    }

    override fun shutdown() {
        container.stop()
    }
}
