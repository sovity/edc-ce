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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationService;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.controlplane.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;

public class WrapperExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "WrapperExtension";

    @Inject
    private AssetIndex assetIndex;
    @Inject
    private AssetService assetService;
    @Inject
    private PolicyDefinitionService policyDefinitionService;
    @Inject
    private ContractAgreementService contractAgreementService;
    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;
    @Inject
    private ContractNegotiationService contractNegotiationService;
    @Inject
    private ContractNegotiationStore contractNegotiationStore;
    @Inject
    private DslContextFactory dslContextFactory;
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
    private TypeTransformerRegistry typeTransformerRegistry;
    @Inject
    private WebService webService;
    @Inject
    private ContractDefinitionService contractDefinitionService;
    @Inject
    private CatalogService catalogService;
    @Inject
    private JsonLd jsonLd;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var objectMapper = typeManager.getMapper(CoreConstants.JSON_LD);
        fixObjectMapperDateSerialization(objectMapper);

        var wrapperExtensionContext = WrapperExtensionContextBuilder.buildContext(
            assetIndex,
            assetService,
            catalogService,
            context.getConfig(),
            contractAgreementService,
            contractAgreementTerminationService,
            contractDefinitionService,
            contractDefinitionStore,
            contractNegotiationService,
            contractNegotiationStore,
            dslContextFactory,
            jsonLd,
            context.getMonitor(),
            objectMapper,
            policyDefinitionService,
            policyDefinitionStore,
            policyEngine,
            transferProcessService,
            transferProcessStore,
            typeTransformerRegistry
        );

        wrapperExtensionContext.managementApiResources().forEach(resource ->
            webService.registerResource(ApiContext.MANAGEMENT, resource));

        wrapperExtensionContext.dspApiResources().forEach(resource ->
            webService.registerResource(ApiContext.PROTOCOL, resource));
    }

    private void fixObjectMapperDateSerialization(ObjectMapper objectMapper) {
        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
