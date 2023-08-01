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
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;

public class WrapperExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "WrapperExtension";
    @Inject
    private AssetIndex assetIndex;
    @Inject
    private AssetService assetService;
    @Inject
    private ContractAgreementService contractAgreementService;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;
    @Inject
    private ContractNegotiationService contractNegotiationService;
    @Inject
    private ContractNegotiationStore contractNegotiationStore;
    @Inject
    private ManagementApiConfiguration dataManagementApiConfiguration;
    @Inject
    private PolicyDefinitionStore policyDefinitionStore;
    @Inject
    private PolicyEngine policyEngine;
    @Inject
    private TransferProcessService transferProcessService;
    @Inject
    private TransferProcessStore transferProcessStore;
    @Inject
    private TypeManager typeManager;
    @Inject
    private WebService webService;
    @Inject
    private ContractAgreementService contractAgreementService;
    @Inject
    private ContractNegotiationStore contractNegotiationStore;
    @Inject
    private TransferProcessService transferProcessService;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var objectMapper = typeManager.getMapper();

        var wrapperExtensionContext = WrapperExtensionContextBuilder.buildContext(
                context,
                assetIndex,
                assetService,
                contractAgreementService,
                contractDefinitionStore,
                contractNegotiationService,
                contractNegotiationStore,
                objectMapper,
                policyDefinitionStore,
                policyEngine,
                transferProcessStore,
                contractAgreementService,
                contractNegotiationStore,
                transferProcessService
        );

        wrapperExtensionContext.jaxRsResources().forEach(resource ->
                webService.registerResource(dataManagementApiConfiguration.getContextAlias(), resource));
    }
}
