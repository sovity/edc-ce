/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.placeholderdatasource;

import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

import static de.sovity.edc.extension.placeholderdatasource.PlaceholderDataSourceExtension.NAME;

@Extension(value = NAME)
@Provides(PlaceholderEndpointService.class)
public class PlaceholderDataSourceExtension implements ServiceExtension {

    public static final String NAME = "Placeholder Data Source";

    @Setting(required = true)
    public static final String MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL = "my.edc.datasource.placeholder.baseurl";

    @Inject
    private DspApiConfiguration dspApiConfiguration;

    @Inject
    private WebService webService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        val controller = new PlaceholderEndpointController();
        webService.registerResource(dspApiConfiguration.getContextAlias(), controller);

        val baseUrl = context.getConfig().getString(MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL);
        context.registerService(PlaceholderEndpointService.class, new PlaceholderEndpointService(baseUrl));
    }
}
