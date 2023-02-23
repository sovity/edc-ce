package de.sovity.edc.extension.postgresql;

import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.configuration.Config;

public class JdbcConnectionProperties {

    @Setting(required = true)
    private static final String DATASOURCE_SETTING_JDBC_URL = "edc.datasource.url";
    @Setting(required = true)
    private static final String DATASOURCE_SETTING_USER = "edc.datasource.user";
    @Setting(required = true)
    private static final String DATASOURCE_SETTING_PASSWORD = "edc.datasource.password";

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public JdbcConnectionProperties(Config config) {
        jdbcUrl = config.getString(DATASOURCE_SETTING_JDBC_URL);
        user = config.getString(DATASOURCE_SETTING_USER);
        password = config.getString(DATASOURCE_SETTING_PASSWORD);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
