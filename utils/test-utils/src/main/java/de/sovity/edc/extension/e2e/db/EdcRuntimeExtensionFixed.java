/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - improvements
 *
 */

package de.sovity.edc.extension.e2e.db;

import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.junit.testfixtures.TestUtils;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.edc.boot.system.ExtensionLoader.loadMonitor;

/**
 * A JUnit extension for running an embedded EDC runtime as part of a test fixture. A custom gradle task printClasspath
 * is used to determine the runtime classpath of the module to run. The runtime obtains a classpath determined by the
 * Gradle build.
 * <p>
 * This extension attaches an EDC runtime to the {@link BeforeTestExecutionCallback} and
 * {@link AfterTestExecutionCallback} lifecycle hooks. Parameter injection of runtime services is supported.
 */
public class EdcRuntimeExtensionFixed extends EdcExtension {
    private static final Monitor MONITOR = loadMonitor();

    private final String moduleName;
    private final String logPrefix;
    private final Map<String, String> properties;
    private Thread runtimeThread;

    public EdcRuntimeExtensionFixed(String moduleName, String logPrefix, Map<String, String> properties) {
        this.moduleName = moduleName;
        this.logPrefix = logPrefix;
        this.properties = Map.copyOf(properties);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        // Find the project root directory, moving up the directory tree
        var root = TestUtils.findBuildRoot();

        // Run a Gradle custom task to determine the runtime classpath of the module to run
        String[] command = {
                new File(root, TestUtils.GRADLE_WRAPPER).getCanonicalPath(),
                "-q",
                moduleName + ":printClasspath"
        };
        Process exec = Runtime.getRuntime().exec(command);
        var classpathString = new String(exec.getInputStream().readAllBytes());
        var errorOutput = new String(exec.getErrorStream().readAllBytes());
        if (exec.waitFor() != 0) {
            throw new EdcException(format("Failed to run gradle command: [%s]. Output: %s %s",
                    String.join(" ", command), classpathString, errorOutput));
        }

        // Replace subproject JAR entries with subproject build directories in classpath.
        // This ensures modified classes are picked up without needing to rebuild dependent JARs.

        var splitRegex = ":|\\s";
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            splitRegex = ";|\\s";
        }

        var classPathEntries = Arrays.stream(classpathString.split(splitRegex))
                .filter(s -> !s.isBlank())
                .flatMap(p -> resolveClassPathEntry(root, p))
                .toArray(URL[]::new);

        // Create a ClassLoader that only has the target module class path, and is not
        // parented with the current ClassLoader.
        var classLoader = URLClassLoader.newInstance(classPathEntries, ClassLoader.getSystemClassLoader());

        // Temporarily inject system properties.
        var savedProperties = (Properties) System.getProperties().clone();
        properties.forEach(System::setProperty);

        var latch = new CountDownLatch(1);

        runtimeThread = new Thread(() -> {
            try {

                // Make the ClassLoader available to the ServiceLoader.
                // This ensures the target module's extensions are discovered and loaded at runtime boot.
                Thread.currentThread().setContextClassLoader(classLoader);

                // Boot EDC runtime.
                super.beforeTestExecution(extensionContext);

                latch.countDown();
            } catch (Exception e) {
                throw new EdcException(e);
            }
        });

        MONITOR.info("Starting module " + moduleName);
        // Start thread and wait for EDC to start up.
        runtimeThread.start();

        if (!latch.await(20, SECONDS)) {
            throw new EdcException("Failed to start EDC runtime");
        }

        MONITOR.info("Module " + moduleName + " started");
        // Restore system properties.
        System.setProperties(savedProperties);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (runtimeThread != null) {
            runtimeThread.join();
        }
        super.afterTestExecution(context);
    }

    @Override
    protected @NotNull Monitor createMonitor() {
        // disable logs when "quiet" log level is set
        if (System.getProperty("org.gradle.logging.level") != null) {
            return new Monitor() {
            };
        } else {
            return new ConsoleMonitor(logPrefix, ConsoleMonitor.Level.DEBUG);
        }
    }

    /**
     * Replace Gradle subproject JAR entries with subproject build directories in classpath. This ensures modified
     * classes are picked up without needing to rebuild dependent JARs.
     *
     * @param root           project root directory.
     * @param classPathEntry class path entry to resolve.
     * @return resolved class path entries for the input argument.
     */
    private Stream<URL> resolveClassPathEntry(File root, String classPathEntry) {
        try {
            File f = new File(classPathEntry).getCanonicalFile();

            // If class path entry is not a JAR under the root (i.e. a sub-project), do not transform it
            boolean isUnderRoot = f.getCanonicalPath().startsWith(root.getCanonicalPath() + File.separator);
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
