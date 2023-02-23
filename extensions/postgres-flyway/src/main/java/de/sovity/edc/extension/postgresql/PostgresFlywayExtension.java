package de.sovity.edc.extension.postgresql;

import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.sql.datasource.ConnectionFactoryDataSource;

import java.util.List;

public class PostgresFlywayExtension implements ServiceExtension {

    private static final List<String> ENTITY_NAME_LIST = List.of(
            "asset",
            "contractdefinition",
            "contractnegotiation",
            "dataplaneinstance",
            "policy",
            "transferprocess");

    private FlywayService flywayService;

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        initFlywayService(context);
        migrateEntityList();
    }

    private void initFlywayService(ServiceExtensionContext context) {
        var jdbcConnectionProperties = new JdbcConnectionProperties(context.getConfig());
        var connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionProperties);
        var dataSource = new ConnectionFactoryDataSource(connectionFactory);
        flywayService = new FlywayService(context.getMonitor(), dataSource);
    }

    private void migrateEntityList() {
        for (String entityName : ENTITY_NAME_LIST) {
            flywayService.migrateDatabase(entityName);
        }
    }

}
