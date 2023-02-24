package de.sovity.edc.extension.postgresql;

import de.sovity.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.sovity.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var flywayService = new FlywayService(context.getMonitor());
        var migrationManager = new DatabaseMigrationManager(context.getConfig(), flywayService);
        migrationManager.migrateAllDataSources();
    }

}
