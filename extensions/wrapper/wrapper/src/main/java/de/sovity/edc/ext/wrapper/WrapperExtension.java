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

package de.sovity.edc.ext.wrapper;

import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class WrapperExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "WrapperExtension";
    @Inject
    private ManagementApiConfiguration dataManagementApiConfiguration;
    @Inject
    private WebService webService;
    @Inject
    private AssetIndex assetIndex;
    @Inject
    private PolicyDefinitionStore policyDefinitionStore;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;
    @Inject
    private TransferProcessStore transferProcessStore;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var wrapperExtensionContextBuilder = new WrapperExtensionContextBuilder(context.getConfig());
        var wrapperExtensionContext = wrapperExtensionContextBuilder.buildContext();

        wrapperExtensionContext.jaxRsResources().forEach(resource ->
                webService.registerResource(dataManagementApiConfiguration.getContextAlias(), resource));
    }
}
