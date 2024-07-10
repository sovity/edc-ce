package de.sovity.edc.extension.e2e.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class EdcRuntimeExtensionWithTestDatabase
        implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String moduleName;
    private final String logPrefix;

    @Getter
    @Delegate(types = {AfterAllCallback.class})
    private final TestDatabase testDatabase = new TestDatabaseViaTestcontainers();

    private final Function<TestDatabase, Map<String, String>> propertyFactory;

    @Delegate(types = {
            BeforeTestExecutionCallback.class,
            AfterTestExecutionCallback.class,
            ParameterResolver.class
    })
    @Getter
    private EdcRuntimeExtensionFixed edcRuntimeExtension = null;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        testDatabase.beforeAll(extensionContext);
        edcRuntimeExtension = new EdcRuntimeExtensionFixed(moduleName, logPrefix, propertyFactory.apply(testDatabase));
    }
}
