/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db.testcontainers

import de.sovity.edc.ce.versions.GradleVersionsCe
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.testcontainers.containers.PostgreSQLContainer

@Provides(PostgresTestcontainer::class)
class PostgresTestcontainerExtension : ServiceExtension {
    private lateinit var container: PostgreSQLContainer<*>

    override fun initialize(context: ServiceExtensionContext) {
        val monitor = context.monitor

        monitor.info("Starting PostgreSQL Testcontainer")
        container = PostgreSQLContainer(GradleVersionsCe.POSTGRES_IMAGE_TAG)
            .withUsername("edc")
            .withPassword("edc")
            .withDatabaseName("edc")
            .also { container ->
                RuntimeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INITDB_ARGS.getStringOrNull(context.config)?.let { args ->
                    container.withEnv("POSTGRES_INITDB_ARGS", args)
                }
                RuntimeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INIT_SCRIPT.getStringOrNull(context.config)?.let { args ->
                    container.withInitScript(args)
                }
            }

        container.start()
        monitor.info("PostgreSQL Testcontainer started!")

        context.registerService(
            PostgresTestcontainer::class.java,
            PostgresTestcontainer(container)
        )
    }

    override fun shutdown() {
        container.stop()
    }
}
