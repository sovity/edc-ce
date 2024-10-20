package de.sovity.edc.extension.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.utils.config.CeConfigProps;
import lombok.val;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.flywaydb.core.api.FlywayException;

public class FlywayMigrator {

    private final PostgresFlywayConfig config;
    private final HikariDataSource dataSource;
    private final Monitor monitor;
    private final FlywayFactory flywayFactory;

    public FlywayMigrator(PostgresFlywayConfig config, HikariDataSource dataSource, Monitor monitor) {
        this.config = config;
        this.dataSource = dataSource;
        this.monitor = monitor;
        this.flywayFactory = new FlywayFactory(config);
    }

    public void updateDatabaseWithLegacyHandling() {
        cleanDatabase();
        updateDatabase();
    }

    private void cleanDatabase() {

        boolean shouldClean = config.flywayClean();
        boolean canClean = config.flywayCleanEnabled();
        if (shouldClean) {
            if (!canClean) {
                throw new IllegalStateException("In order to clean the history both %s and %s must be set to true.".formatted(
                        CeConfigProps.EDC_FLYWAY_CLEAN.getProperty(), CeConfigProps.EDC_FLYWAY_CLEAN_ENABLE.getProperty()
                ));
            }
            monitor.info(() -> "Cleaning database before migrations, since %s=true and %s=true.".formatted(
                CeConfigProps.EDC_FLYWAY_CLEAN.getProperty(), CeConfigProps.EDC_FLYWAY_CLEAN_ENABLE.getProperty()
            ));

            val flyway = flywayFactory.setupFlywayForUnifiedHistory(dataSource);
            flyway.clean();
        }
    }

    private void updateDatabase() {
        val flyway = flywayFactory.setupFlywayForUnifiedHistory(dataSource);

        try {
            flyway.migrate();
        } catch (FlywayException e) {
            throw migrationFailed(e);
        }
    }

    private EdcPersistenceException migrationFailed(Exception e) {
        return new EdcPersistenceException("Flyway migration failed", e);
    }
}
