package de.sovity.edc.extension.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.extension.postgresql.legacy.migration.DatabaseMigrationManager;
import de.sovity.edc.extension.postgresql.legacy.migration.FlywayService;
import de.sovity.edc.extension.postgresql.utils.DatabaseUtils;
import lombok.val;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.system.configuration.Config;
import org.flywaydb.core.api.FlywayException;

import java.sql.SQLException;

public class FlywayMigrator {

    private final PostgresFlywayConfig config;
    private final Config legacyConfig;
    private final HikariDataSource dataSource;
    private final Monitor monitor;
    private final FlywayFactory flywayFactory;
    private final DatabaseUtils databaseUtils;

    public FlywayMigrator(PostgresFlywayConfig config, Config legacyConfig, HikariDataSource dataSource, Monitor monitor) {
        this.config = config;
        this.legacyConfig = legacyConfig;
        this.dataSource = dataSource;
        this.monitor = monitor;
        this.flywayFactory = new FlywayFactory(config);
        this.databaseUtils = new DatabaseUtils(dataSource);
    }

    public void updateDatabaseWithLegacyHandling() {
        migrateLegacyHistory();
        cleanDatabase();
        updateDatabase();
    }

    private void migrateLegacyHistory() {
        if (config.flywayClean() && config.flywayCleanEnabled()) {
            return;
        }
        try {
            val isLegacyDatabase = databaseUtils.hasLegacyMigrations();
            if (!isLegacyDatabase) {
                return;
            }
        } catch (SQLException e) {
            throw migrationFailed(e);
        }

        monitor.debug(() -> "Legacy-style flyway migration table detected. Upgrading to the latest legacy database history revision.");
        val flywayService = new FlywayService(
                monitor,
                config.flywayRepair(),
                config.flywayCleanEnabled(),
                config.flywayClean()
        );
        val migrationManager = new DatabaseMigrationManager(legacyConfig, flywayService);
        migrationManager.migrateAllDataSources();

        monitor.debug(() -> "Upgrading to the latest unified database history revision.");
        val flyway = flywayFactory.setupFlywayForUnifiedHistoryFromLegacyDatabase(dataSource);

        try {
            flyway.baseline();
        } catch (FlywayException e) {
            throw migrationFailed(e);
        }
    }

    private void cleanDatabase() {

        boolean shouldClean = config.flywayClean();
        boolean canClean = config.flywayCleanEnabled();
        if (shouldClean) {
            if (!canClean) {
                throw new IllegalStateException("In order to clean the history both %s and %s must be set to true.".formatted(
                        PostgresFlywayExtension.FLYWAY_CLEAN, PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE
                ));
            }
            monitor.info(() -> "Cleaning database before migrations, since %s=true and %s=true.".formatted(
                    PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE, PostgresFlywayExtension.FLYWAY_CLEAN
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
