package de.sovity.edc.extension.e2e.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class EdcRuntimeExtensionWithTestDatabase
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String moduleName;
    private final String logPrefix;

    private DSLContext dslContext;

    @Getter
    @Delegate(types = {AfterAllCallback.class})
    private final TestDatabase testDatabase = new TestDatabaseViaTestcontainers();

    private final Function<TestDatabase, Map<String, String>> propertyFactory;

    @Delegate(types = {
        BeforeTestExecutionCallback.class,
        AfterTestExecutionCallback.class
    })
    @Getter
    private EdcRuntimeExtensionFixed edcRuntimeExtension = null;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        testDatabase.beforeAll(extensionContext);
        edcRuntimeExtension = new EdcRuntimeExtensionFixed(moduleName, logPrefix, propertyFactory.apply(testDatabase));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        boolean isJooqDsl = parameterContext.getParameter().getType().equals(DSLContext.class);
        return isJooqDsl || edcRuntimeExtension.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        boolean isJooqDsl = parameterContext.getParameter().getType().equals(DSLContext.class);
        if (isJooqDsl) {
            return getDslContext().dsl();
        } else {
            return edcRuntimeExtension.resolveParameter(parameterContext, extensionContext);
        }
    }

    private synchronized DSLContext getDslContext() {
        if (dslContext == null) {
            val credentials = testDatabase.getJdbcCredentials();
            dslContext = DSL.using(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword());
        }

        return dslContext;
    }
}
