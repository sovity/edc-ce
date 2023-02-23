package de.sovity.edc.extension.postgresql;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.sql.datasource.ConnectionFactoryDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.MigrateResult;

public class FlywayService {

    private static final String MIGRATION_LOCATION_BASE = "classpath:migration";

    private final Monitor monitor;
    private final ConnectionFactoryDataSource dataSource;

    public FlywayService(Monitor monitor, ConnectionFactoryDataSource dataSource) {
        this.monitor = monitor;
        this.dataSource = dataSource;
    }

    public void migrateDatabase(String entityName) {
        final var flyway = Flyway.configure()
                .baselineVersion(MigrationVersion.fromVersion("0.0.0"))
                .failOnMissingLocations(true)
                .dataSource(dataSource)
                .table(String.format("flyway_schema_history_%s", entityName))
                .locations(String.join("/", MIGRATION_LOCATION_BASE, entityName))
                .load();
        flyway.baseline();
        var migrateResult = flyway.migrate();
        handleFlywayResult(entityName, migrateResult);
    }

    private void handleFlywayResult(String entityName, MigrateResult migrateResult) {
        if (migrateResult.success) {
            monitor.info(String.format(
                    "Successfully migrated database for entity %s from version %s to version %s",
                    entityName,
                    migrateResult.initialSchemaVersion,
                    migrateResult.targetSchemaVersion));
        } else {
            var warnings = String.join(", ", migrateResult.warnings);
            throw new EdcPersistenceException(String.format(
                    "Migration for entity %s failed: %s",
                    entityName,
                    warnings));
        }
    }

}
