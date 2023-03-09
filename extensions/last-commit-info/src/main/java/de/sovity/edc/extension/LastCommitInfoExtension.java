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

package de.sovity.edc.extension;

import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class LastCommitInfoExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "LastCommitInfoExtension";
    @Inject
    private ManagementApiConfiguration config;
    @Inject
    private WebService webService;


    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var lastCommitInfoService = new LastCommitInfoService(context);
        var controller = new LastCommitInfoController(lastCommitInfoService);
        webService.registerResource(config.getContextAlias(), controller);
    }
}
