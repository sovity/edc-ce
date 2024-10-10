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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Starts one EDC that boots/shutdowns for each test method
 * <p>
 * Modified {@link org.eclipse.edc.junit.extensions.RuntimePerClassExtension}
 * that uses {@link EmbeddedRuntimeFixed} instead of {@link org.eclipse.edc.junit.extensions.EmbeddedRuntime}.
 */
public class RuntimePerClassExtensionFixed extends RuntimeExtensionFixed implements BeforeAllCallback, AfterAllCallback {

    public RuntimePerClassExtensionFixed(EmbeddedRuntimeFixed runtime) {
        super(runtime);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        runtime.boot(false);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        runtime.shutdown();
    }
}
