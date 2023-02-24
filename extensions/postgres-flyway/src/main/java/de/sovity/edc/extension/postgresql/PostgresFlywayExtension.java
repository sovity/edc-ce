package de.sovity.edc.extension.postgresql;

import de.sovity.edc.extension.postgresql.connection.JdbcConnectionProperties;
import de.sovity.edc.extension.postgresql.flyway.FlywayService;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;

public class PostgresFlywayExtension implements ServiceExtension {

    private static final String EDC_DATASOURCE_PREFIX = "edc.datasource";

    private FlywayService flywayService;

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        flywayService = new FlywayService(context.getMonitor());
        migrateEntityList(context.getConfig());
    }

    private void migrateEntityList(Config config) {
        for (String datasourceName : getDataSourceNames(config)) {
            var jdbcConnectionProperties = new JdbcConnectionProperties(config, datasourceName);
            flywayService.migrateDatabase(datasourceName, jdbcConnectionProperties);
        }
    }

    private List<String> getDataSourceNames(Config config) {
        var edcDatasourceConfig = config.getConfig(EDC_DATASOURCE_PREFIX);
        return edcDatasourceConfig.partition().toList().stream()
                .map(Config::currentNode)
                .toList();
    }

}
