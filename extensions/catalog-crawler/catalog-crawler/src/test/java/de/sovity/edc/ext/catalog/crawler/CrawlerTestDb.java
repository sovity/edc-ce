package de.sovity.edc.ext.catalog.crawler;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.config.FlywayService;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.postgresql.FlywayUtils;
import de.sovity.edc.extension.postgresql.HikariDataSourceFactory;
import de.sovity.edc.extension.postgresql.JdbcCredentials;
import org.jooq.DSLContext;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.function.Consumer;

public class CrawlerTestDb implements BeforeAllCallback, AfterAllCallback {
    private final TestDatabaseViaTestcontainers db = new TestDatabaseViaTestcontainers();

    private HikariDataSource dataSource = null;
    private DslContextFactory dslContextFactory = null;

    public void testTransaction(Consumer<DSLContext> code) {
        dslContextFactory.testTransaction(code);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // Init DB
        db.beforeAll(extensionContext);

        // Init Data Source
        var credentials = new JdbcCredentials(
                db.getJdbcCredentials().jdbcUrl(),
                db.getJdbcCredentials().jdbcUser(),
                db.getJdbcCredentials().jdbcPassword()
        );
        dataSource = HikariDataSourceFactory.newDataSource(credentials, 10, 1000);
        dslContextFactory = new DslContextFactory(dataSource);

        // Migrate DB
        var params = FlywayService.baseConfig("classpath:db-crawler/migration-test-utils")
                .migrate(true)
                .build();
        try {
            FlywayUtils.cleanAndMigrate(params, dataSource);
        } catch (Exception e) {
            var paramsWithClean = params.withClean(true).withCleanEnabled(true);
            FlywayUtils.cleanAndMigrate(paramsWithClean, dataSource);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }

        // Close DB
        db.afterAll(extensionContext);
    }
}
