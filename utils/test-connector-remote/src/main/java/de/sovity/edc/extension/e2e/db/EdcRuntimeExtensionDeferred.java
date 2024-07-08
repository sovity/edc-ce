package de.sovity.edc.extension.e2e.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class EdcRuntimeExtensionDeferred
        implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String moduleName;
    private final String logPrefix;

    private final Supplier<Map<String, String>> propertyFactory;

    @Delegate(types = {
            BeforeTestExecutionCallback.class,
            AfterTestExecutionCallback.class,
            ParameterResolver.class
    })
    @Getter
    private EdcRuntimeExtensionFixed edcRuntimeExtensionFixed = null;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        edcRuntimeExtensionFixed = new EdcRuntimeExtensionFixed(moduleName, logPrefix, propertyFactory.get());
    }
}
