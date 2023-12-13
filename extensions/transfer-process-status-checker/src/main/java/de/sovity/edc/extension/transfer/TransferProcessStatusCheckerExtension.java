/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.transfer;

import org.eclipse.edc.connector.transfer.spi.status.StatusCheckerRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.List;

public class TransferProcessStatusCheckerExtension implements ServiceExtension {
    private static final String EXTENSION_NAME = "Transfer Process Status Checker";

    @Inject
    private StatusCheckerRegistry statusCheckerRegistry;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        List.of("HttpProxy", "HttpData").forEach(this::registerStatusChecker);
    }

    private void registerStatusChecker(String transferType) {
        statusCheckerRegistry.register(transferType, (transferProcess, resources) -> true);
    }
}
