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

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


/**
 * Starts one EDC that boots/shutdowns once per test class
 * <p>
 * Modified {@link org.eclipse.edc.junit.extensions.RuntimePerMethodExtension}
 * that uses {@link EmbeddedRuntimeFixed} instead of {@link org.eclipse.edc.junit.extensions.EmbeddedRuntime}.
 */
public class RuntimePerMethodExtensionFixed extends RuntimeExtensionFixed
    implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    public RuntimePerMethodExtensionFixed(EmbeddedRuntimeFixed runtime) {
        super(runtime);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        runtime.boot(false);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        runtime.shutdown();
    }
}
