/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ce.api.control.OnDemandAssetDataSourceController;
import de.sovity.edc.ce.api.ui.UiResourceImpl;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.OwnConnectorEndpointServiceImpl;
import de.sovity.edc.ce.api.usecase.UseCaseResourceImpl;
import de.sovity.edc.ce.libs.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ce.modules.db.DslContextFactory;
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractAgreementTerminationService;
import de.sovity.edc.ce.modules.policy_utils.always_true.AlwaysTruePolicyDefinitionService;
import de.sovity.edc.runtime.config.ConfigUtils;
import de.sovity.edc.runtime.simple_di.SimpleDi;
import kotlin.Unit;
import lombok.val;
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
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;

/**
 * EDC extension which provides API implementations for most of the Wrapper API endpoints.
 * Excluded endpoints are either implemented in other sovity Community Edition EDC Extensions
 * or implemented in our Enterprise Edition.
 * <p>
 * More information available at<br>
 * /docs/product/api_wrapper.md<br>
 * /docs/dev/wrapper-api.md
 */
public class CeApiExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "WrapperExtension";

    @Inject
    private TypeManager typeManager;
    @Inject
    private WebService webService;


    @Inject
    private AlwaysTruePolicyDefinitionService alwaysTruePolicyDefinitionService;
    @Inject
    private AssetIndex assetIndex;
    @Inject
    private AssetService assetService;
    @Inject
    private CatalogService catalogService;
    @Inject
    private ConfigUtils configUtils;
    @Inject
    private ContractAgreementService contractAgreementService;
    @Inject
    private ContractNegotiationStore contractNegotiationStore;
    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;
    @Inject
    private ContractDefinitionService contractDefinitionService;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;
    @Inject
    private ContractNegotiationService contractNegotiationService;
    @Inject
    private DslContextFactory dslContextFactory;
    @Inject
    private JsonLd jsonLd;
    @Inject
    private PolicyDefinitionService policyDefinitionService;
    @Inject
    private PolicyDefinitionStore policyDefinitionStore;
    @Inject
    private PolicyEngine policyEngine;
    @Inject
    private RuleBindingRegistry ruleBindingRegistry;
    @Inject
    private TransferProcessService transferProcessService;
    @Inject
    private TransferProcessStore transferProcessStore;
    @Inject
    private TypeTransformerRegistry typeTransformerRegistry;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var objectMapper = typeManager.getMapper(CoreConstants.JSON_LD);
        fixObjectMapperDateSerialization(objectMapper);

        @SuppressWarnings("unchecked")
        val instances = new SimpleDi()
            .addAllowedPackage(
                CeApiExtension.class.getPackageName(),
                "de.sovity.edc.ce.libs.mappers"
            )
            .addClassesToInstantiate(
                // the order is important here
                ExpressionExtractor.class,
                OwnConnectorEndpointServiceImpl.class,
                UiResourceImpl.class,
                UseCaseResourceImpl.class,
                OnDemandAssetDataSourceController.class
            )
            .addInstances(
                context.getConfig(),
                context.getMonitor(),
                alwaysTruePolicyDefinitionService,
                assetIndex,
                assetService,
                catalogService,
                configUtils,
                contractNegotiationStore,
                contractAgreementTerminationService,
                contractDefinitionService,
                contractDefinitionStore,
                contractNegotiationService,
                dslContextFactory,
                jsonLd,
                policyDefinitionService,
                policyDefinitionStore,
                policyEngine,
                ruleBindingRegistry,
                transferProcessService,
                transferProcessStore,
                typeTransformerRegistry,
                objectMapper
            )
            .addInstance(contractAgreementService)
            .onInstanceCreated(instance -> {
                //noinspection unchecked
                context.registerService((Class<Object>) instance.getClass(), instance);
                return Unit.INSTANCE;
            })
            .toInstances();

        webService.registerResource(ApiContext.MANAGEMENT, instances.getSingle(UiResourceImpl.class));
        webService.registerResource(ApiContext.MANAGEMENT, instances.getSingle(UseCaseResourceImpl.class));

        webService.registerResource(ApiContext.PROTOCOL, instances.getSingle(OnDemandAssetDataSourceController.class));
    }

    private void fixObjectMapperDateSerialization(ObjectMapper objectMapper) {
        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
