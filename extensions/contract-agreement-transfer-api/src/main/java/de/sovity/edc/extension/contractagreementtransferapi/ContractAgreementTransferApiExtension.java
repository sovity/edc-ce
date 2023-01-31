/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.extension.contractagreementtransferapi;

import de.sovity.edc.extension.contractagreementtransferapi.controller.ContractAgreementTransferApiController;
import de.sovity.edc.extension.contractagreementtransferapi.service.ContractNegotiationByAgreementService;
import de.sovity.edc.extension.contractagreementtransferapi.service.DataRequestService;
import org.eclipse.edc.api.transformer.DtoTransformerRegistry;
import org.eclipse.edc.connector.api.datamanagement.configuration.DataManagementApiConfiguration;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class ContractAgreementTransferApiExtension implements ServiceExtension {

    public static final String NAME = "Management API: Contract Agreement Transfer";
    @Inject
    private WebService webService;

    @Inject
    private DataManagementApiConfiguration config;

    @Inject
    private DtoTransformerRegistry transformerRegistry;

    @Inject
    private ContractAgreementService contractAgreementService;

    @Inject
    private ContractNegotiationService contractNegotiationService;

    @Inject
    private TransferProcessService transferProcessService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        var contractNegotiationByAgreementService =
                new ContractNegotiationByAgreementService(contractNegotiationService);
        var dataRequestService = new DataRequestService(
                contractAgreementService,
                contractNegotiationByAgreementService);
        var controller = new ContractAgreementTransferApiController(
                monitor,
                transferProcessService,
                transformerRegistry,
                dataRequestService);
        webService.registerResource(config.getContextAlias(), controller);
    }
}
