/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.db.flyway.FlywayFactory
import de.sovity.edc.ce.modules.db.flyway.FlywayMigrator
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import de.sovity.edc.ce.modules.db.pool.ConnectionPoolFactory
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.system.configuration.Config
import org.eclipse.edc.sql.ConnectionFactory
import org.eclipse.edc.sql.datasource.ConnectionPoolDataSource
import org.eclipse.edc.sql.pool.commons.CommonsConnectionPool
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry
import org.eclipse.edc.transaction.spi.TransactionContext
import org.flywaydb.core.Flyway

@Provides(DslContextFactory::class)
class DbExtension : ServiceExtension {
    @Inject
    private lateinit var dataSourceRegistry: DataSourceRegistry

    @Inject
    private lateinit var connectionFactory: ConnectionFactory

    @Inject
    private lateinit var transactionContext: TransactionContext

    @Inject(required = false)
    private var postgresTestcontainer: PostgresTestcontainer? = null

    private var pool: CommonsConnectionPool? = null

    override fun initialize(context: ServiceExtensionContext) {
        val monitor = context.monitor
        val config = context.config

        val jdbcCredentials = getJdbcCredentials(config)
        pool = ConnectionPoolFactory.newInstance(config, monitor, jdbcCredentials, connectionFactory)
        dataSourceRegistry.register("default", ConnectionPoolDataSource(pool))
        val dataSource = dataSourceRegistry.resolve("default") // will be now wrapped with transaction handling

        val flyway = FlywayFactory.newFlyway(config, jdbcCredentials)
        context.registerService(Flyway::class.java, flyway)
        FlywayMigrator(config, monitor).cleanAndMigrate(flyway)

        val dslContextFactory = DslContextFactory(dataSource, transactionContext)
        context.registerService(DslContextFactory::class.java, dslContextFactory)
    }

    private fun getJdbcCredentials(config: Config): JdbcCredentials {
        if (postgresTestcontainer != null) {
            return postgresTestcontainer!!.getJdbcCredentials()
        }

        return JdbcCredentials(
            CeConfigProps.SOVITY_JDBC_URL.getStringOrThrow(config),
            CeConfigProps.SOVITY_JDBC_USER.getStringOrThrow(config),
            CeConfigProps.SOVITY_JDBC_PASSWORD.getStringOrThrow(config)
        )
    }

    data class JdbcCredentials(val url: String, val user: String, val password: String)
}
