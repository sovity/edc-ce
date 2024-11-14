/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package de.sovity.edc.extension.e2e.junit.edc;

import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.utils.config.SovityEdcRuntime;
import de.sovity.edc.utils.config.model.ConfigProp;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.junit.extensions.EmbeddedRuntime;
import org.eclipse.edc.junit.extensions.TestServiceExtensionContext;
import org.eclipse.edc.junit.testfixtures.TestUtils;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.SystemExtension;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.edc.boot.system.ExtensionLoader.loadMonitor;

/**
 * Starts one EDC
 * <p>
 * Modified {@link EmbeddedRuntime}. All changes to the file are marked with "Begin of modified code" / "End of modified code" blocks.
 */
public class EmbeddedRuntimeFixed extends SovityEdcRuntime {

    private static final Monitor MONITOR = loadMonitor();

    private final String name;
    private final Map<String, String> properties;
    private final String[] additionalModules;
    private final LinkedHashMap<Class<?>, Object> serviceMocks = new LinkedHashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MultiSourceServiceLocator serviceLocator;

    /* Begin of modified code: Custom Constructors */

    public EmbeddedRuntimeFixed(
        String name,
        ConnectorBootConfig connectorBootConfig,
        List<ConfigProp> allConfigProps,
        String... additionalModules
    ) {
        this(
            new MultiSourceServiceLocator(),
            name,
            connectorBootConfig,
            allConfigProps,
            additionalModules
        );
    }

    private EmbeddedRuntimeFixed(
        MultiSourceServiceLocator serviceLocator,
        String name,
        ConnectorBootConfig connectorBootConfig,
        List<ConfigProp> allConfigProps,
        String... additionalModules
    ) {
        super(serviceLocator, allConfigProps);
        this.serviceLocator = serviceLocator;
        this.name = name;
        this.additionalModules = additionalModules;
        this.properties = connectorBootConfig.asMap();
    }

    /* End of modified code: Custom Constructors */

    @Override
    public void boot(boolean addShutdownHook) {
        try {
            /* Begin of Modified Code: Handle empty additionalModules */
            Validate.notEmpty(additionalModules, "additionalModules must not be empty, as the current classpath is excluded");
            /* End of Modified Code: Handle empty additionalModules */

            // Find the project root directory, moving up the directory tree
            var root = TestUtils.findBuildRoot();

            // Run a Gradle custom task to determine the runtime classpath of the module to run
            var printClasspath = Arrays.stream(additionalModules).map(it -> it + ":printClasspath");
            var commandStream = Stream.of(new File(root, TestUtils.GRADLE_WRAPPER).getCanonicalPath(), "-q");
            var command = Stream.concat(commandStream, printClasspath).toArray(String[]::new);

            var exec = Runtime.getRuntime().exec(command);
            var classpathString = new String(exec.getInputStream().readAllBytes());
            var errorOutput = new String(exec.getErrorStream().readAllBytes());
            if (exec.waitFor() != 0) {
                throw new EdcException(format("Failed to run gradle command: [%s]. Output: %s %s",
                    String.join(" ", command), classpathString, errorOutput));
            }

            /* Begin of modified code: Fix issue under Windows */

            var splitRegex = ":|\\s";
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
                splitRegex = ";|\\s";
            }

            // Replace subproject JAR entries with subproject build directories in classpath.
            // This ensures modified classes are picked up without needing to rebuild dependent JARs.
            var classPathEntries = Arrays.stream(classpathString.split(splitRegex))
                .filter(s -> !s.isBlank())
                .flatMap(p -> resolveClassPathEntry(root, p))
                .toArray(URL[]::new);

            /* End of modified code: Fix issue under Windows */

            // Create a ClassLoader that only has the target module class path, and is not
            // parented with the current ClassLoader.
            var classLoader = URLClassLoader.newInstance(classPathEntries);

            // Temporarily inject system properties.
            var savedProperties = (Properties) System.getProperties().clone();
            properties.forEach(System::setProperty);

            var runtimeException = new AtomicReference<Exception>();
            var latch = new CountDownLatch(1);

            MONITOR.info("Starting runtime %s with additional modules: [%s]".formatted(name, String.join(",", additionalModules)));

            executorService.execute(() -> {
                try {
                    Thread.currentThread().setContextClassLoader(classLoader);

                    super.boot(false);

                    latch.countDown();
                } catch (Exception e) {
                    runtimeException.set(e);
                    throw new EdcException(e);
                }
            });

            if (!latch.await(20, SECONDS)) {
                throw new EdcException("Failed to start EDC runtime", runtimeException.get());
            }

            MONITOR.info("Runtime %s started".formatted(name));
            // Restore system properties.
            System.setProperties(savedProperties);
        } catch (Exception e) {
            throw new EdcException(e);
        }

    }

    @Override
    public void shutdown() {
        serviceLocator.clearSystemExtensions();
        super.shutdown();
    }

    @Override
    protected @NotNull ServiceExtensionContext createContext(Monitor monitor, Config config) {
        return new TestServiceExtensionContext(monitor, config, serviceMocks);
    }

    @Override
    protected @NotNull Monitor createMonitor() {
        // disable logs when "quiet" log level is set
        if (System.getProperty("org.gradle.logging.level") != null) {
            return new Monitor() {
            };
        } else {
            return new ConsoleMonitor(name, ConsoleMonitor.Level.DEBUG, true);
        }
    }

    public <T extends SystemExtension> EmbeddedRuntimeFixed registerSystemExtension(Class<T> type, SystemExtension extension) {
        serviceLocator.registerSystemExtension(type, extension);
        return this;
    }

    public <T> EmbeddedRuntimeFixed registerServiceMock(Class<T> type, T mock) {
        serviceMocks.put(type, mock);
        return this;
    }

    public <T> T getService(Class<T> clazz) {
        return context.getService(clazz);
    }

    public ServiceExtensionContext getContext() {
        return context;
    }

    /**
     * Replace Gradle subproject JAR entries with subproject build directories in classpath. This ensures modified
     * classes are picked up without needing to rebuild dependent JARs.
     *
     * @param root project root directory.
     * @param classPathEntry class path entry to resolve.
     * @return resolved class path entries for the input argument.
     */
    private Stream<URL> resolveClassPathEntry(File root, String classPathEntry) {
        try {
            var f = new File(classPathEntry).getCanonicalFile();

            // If class path entry is not a JAR under the root (i.e. a sub-project), do not transform it
            var isUnderRoot = f.getCanonicalPath().startsWith(root.getCanonicalPath() + File.separator);
            if (!classPathEntry.toLowerCase(Locale.ROOT).endsWith(".jar") || !isUnderRoot) {
                var sanitizedClassPathEntry = classPathEntry.replace("build/resources/main", "src/main/resources");
                return Stream.of(new File(sanitizedClassPathEntry).toURI().toURL());
            }

            // Replace JAR entry with the resolved classes and resources folder
            var buildDir = f.getParentFile().getParentFile();
            return Stream.of(
                new File(buildDir, "/classes/java/main").toURI().toURL(),
                new File(buildDir, "../src/main/resources").toURI().toURL()
            );
        } catch (IOException e) {
            throw new EdcException(e);
        }
    }
}
