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

package de.sovity.edc.extension.dataplaneinstancestoreinit;

import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class DataPlaneInstanceInitializerExtension implements ServiceExtension {
    public static final String EXTENSION_NAME = "VaultInitializer";

    @Inject
    private DataPlaneSelectorService dataPlaneSelectorService;
    private DataPlaneInitializerService dataPlaneInitializerService;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        dataPlaneInitializerService = new DataPlaneInitializerService(
            context.getMonitor(),
            context.getConfig(),
            dataPlaneSelectorService
        );
    }

    @Override
    public void start() {
        dataPlaneInitializerService.addIntegratedDataPlane();
    }
}
