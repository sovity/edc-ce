/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.runtime.modules.isProduction
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.sql.pool.commons.CommonsConnectionPoolServiceExtension

object DbModule {
    /**
     * Setup of the EDC Database:
     *  - EDC Transaction Management
     *  - Flyway
     *  - JooQ
     *  - Testcontainers (Dev / Test)
     */
    fun forMigrationScripts(defaultMigrationLocation: String) = EdcModule(
        name = "db-postgres-flyway-jooq",
        documentation = "EDC SQL Stores, DB Access, JooQ + DslContextFactory, Testcontainers in Dev"
    ).apply {
        dependencyBundle(CeDependencyBundles.postgresql)
        excludeServiceExtensions(CommonsConnectionPoolServiceExtension::class.java)
        serviceExtensions(DbExtension::class.java)

        // Enable Testcontainers in Local Dev and Test
        moduleIf(
            DocumentedFn("`!prod && !jdbcUrl`") { config ->
                !config.isProduction() && CeConfigProps.SOVITY_JDBC_URL.getStringOrEmpty(config) == ""
            },
            PostgresTestcontainerModule.instance(),
            documentation = PostgresTestcontainerModule.instance().documentation
        )

        // Required: DB Connection
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_JDBC_URL
        ) {
            requiredInProd()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_JDBC_USER
        ) {
            requiredInProd()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_JDBC_PASSWORD
        ) {
            requiredInProd()
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_FLYWAY_CLEAN_ENABLE
        ) {
            defaultValue("false")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_FLYWAY_CLEAN
        ) {
            defaultValue("false")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATASOURCE_DEFAULT_NAME
        ) {
            defaultValue("default")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATASOURCE_EDR_NAME
        ) {
            defaultValue("default")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_FLYWAY_MIGRATION_LOCATION
        ) {
            defaultValue(defaultMigrationLocation)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS
        )
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_DB_CONNECTION_POOL_SIZE
        ) {
            defaultValue("30")
        }
    }
}
