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
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.LiteralMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetMapper;
import de.sovity.edc.ext.wrapper.api.ui.UiResourceImpl;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetIdValidator;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.CatalogApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionLiteralMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionOperatorMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationStateService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractOfferMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractNegotiationUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.TransferRequestBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DapsConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.MiwConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import de.sovity.edc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import de.sovity.edc.ext.wrapper.api.usecase.UseCaseResourceImpl;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.PolicyMappingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import de.sovity.edc.utils.catalog.DspCatalogService;
import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.List;


/**
 * Manual Dependency Injection.
 * <p>
 * We want to develop as Java Backend Development is done, but we have no CDI / DI Framework to rely
 * on.
 * <p>
 * EDC {@link Inject} only works in {@link WrapperExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class WrapperExtensionContextBuilder {

    public static WrapperExtensionContext buildContext(
            AssetIndex assetIndex,
            AssetService assetService,
            CatalogService catalogService,
            Config config,
            ContractAgreementService contractAgreementService,
            ContractDefinitionService contractDefinitionService,
            ContractDefinitionStore contractDefinitionStore,
            ContractNegotiationService contractNegotiationService,
            ContractNegotiationStore contractNegotiationStore,
            JsonLd jsonLd,
            ObjectMapper objectMapper,
            PolicyDefinitionService policyDefinitionService,
            PolicyDefinitionStore policyDefinitionStore,
            PolicyEngine policyEngine,
            ServiceExtensionContext serviceExtensionContext,
            TransferProcessService transferProcessService,
            TransferProcessStore transferProcessStore,
            TypeTransformerRegistry typeTransformerRegistry
    ) {
        // UI API
        var operatorMapper = new OperatorMapper();
        var criterionOperatorMapper = new CriterionOperatorMapper();
        var criterionLiteralMapper = new CriterionLiteralMapper();
        var criterionMapper = new CriterionMapper(criterionOperatorMapper, criterionLiteralMapper);
        var literalMapper = new LiteralMapper(objectMapper);
        var atomicConstraintMapper = new AtomicConstraintMapper(literalMapper, operatorMapper);
        var policyValidator = new PolicyValidator();
        var constraintExtractor = new ConstraintExtractor(policyValidator, atomicConstraintMapper);
        var policyMapper = new PolicyMapper(
                constraintExtractor,
                atomicConstraintMapper,
                typeTransformerRegistry);
        var edcPropertyUtils = new EdcPropertyUtils();
        var uiAssetMapper = new UiAssetMapper(edcPropertyUtils);
        var assetMapper = new AssetMapper(typeTransformerRegistry, uiAssetMapper, jsonLd);
        var transferProcessStateService = new TransferProcessStateService();
        var selfDescriptionService = new SelfDescriptionService(config);
        var contractNegotiationUtils = new ContractNegotiationUtils(
                contractNegotiationService,
                selfDescriptionService
        );
        var contractAgreementPageCardBuilder = new ContractAgreementPageCardBuilder(
                policyMapper,
                transferProcessStateService,
                assetMapper,
                contractNegotiationUtils
        );
        var contractAgreementDataFetcher = new ContractAgreementDataFetcher(
                contractAgreementService,
                contractNegotiationStore,
                transferProcessService,
                assetIndex
        );
        var contractAgreementApiService = new ContractAgreementPageApiService(
                contractAgreementDataFetcher,
                contractAgreementPageCardBuilder
        );
        var contactDefinitionBuilder = new ContractDefinitionBuilder(criterionMapper);
        var contractDefinitionApiService = new ContractDefinitionApiService(
                contractDefinitionService,
                criterionMapper,
                contactDefinitionBuilder);
        var transferHistoryPageApiService = new TransferHistoryPageApiService(
                assetService,
                contractAgreementService,
                contractNegotiationStore,
                transferProcessService,
                transferProcessStateService
        );
        var transferHistoryPageAssetFetcherService = new TransferHistoryPageAssetFetcherService(
                assetService,
                transferProcessService,
                assetMapper,
                contractNegotiationStore,
                contractNegotiationUtils
        );
        var contractAgreementUtils = new ContractAgreementUtils(contractAgreementService);
        var assetIdValidator = new AssetIdValidator();
        var assetApiService = new AssetApiService(
                assetService,
                assetMapper,
                assetIdValidator,
                selfDescriptionService
        );
        var transferRequestBuilder = new TransferRequestBuilder(
                objectMapper,
                contractAgreementUtils,
                contractNegotiationUtils,
                edcPropertyUtils,
                serviceExtensionContext.getConnectorId()
        );
        var contractAgreementTransferApiService = new ContractAgreementTransferApiService(
                transferRequestBuilder,
                transferProcessService
        );
        var policyDefinitionApiService = new PolicyDefinitionApiService(
                policyDefinitionService,
                policyMapper
        );
        var dataOfferBuilder = new DspDataOfferBuilder(jsonLd);
        var dspCatalogService = new DspCatalogService(catalogService, dataOfferBuilder);
        var catalogApiService = new CatalogApiService(
                assetMapper,
                policyMapper,
                dspCatalogService,
                selfDescriptionService
        );
        var contractOfferMapper = new ContractOfferMapper(policyMapper);
        var contractNegotiationBuilder = new ContractNegotiationBuilder(contractOfferMapper);
        var contractNegotiationStateService = new ContractNegotiationStateService();
        var contractNegotiationApiService = new ContractNegotiationApiService(
                contractNegotiationService,
                contractNegotiationBuilder,
                contractNegotiationStateService
        );
        var miwConfigBuilder = new MiwConfigService(config);
        var dapsConfigBuilder = new DapsConfigService(config);
        var dashboardDataFetcher = new DashboardDataFetcher(
                contractNegotiationStore,
                transferProcessService,
                assetIndex,
                policyDefinitionService
        );
        var dashboardApiService = new DashboardPageApiService(
                dashboardDataFetcher,
                transferProcessStateService,
                dapsConfigBuilder,
                miwConfigBuilder,
                selfDescriptionService
        );
        var uiResource = new UiResourceImpl(
                contractAgreementApiService,
                contractAgreementTransferApiService,
                transferHistoryPageApiService,
                transferHistoryPageAssetFetcherService,
                assetApiService,
                policyDefinitionApiService,
                catalogApiService,
                contractDefinitionApiService,
                contractNegotiationApiService,
                dashboardApiService
        );

        // Use Case API
        var kpiApiService = new KpiApiService(
                assetIndex,
                policyDefinitionStore,
                contractDefinitionStore,
                transferProcessStore,
                contractAgreementService,
                transferProcessStateService
        );
        var supportedPolicyApiService = new SupportedPolicyApiService(policyEngine);
        var policyMappingService = new PolicyMappingService();
        var offeringService = new OfferingService(
                assetIndex,
                policyDefinitionStore,
                contractDefinitionStore,
                policyMappingService,
                edcPropertyUtils
        );
        var useCaseResource = new UseCaseResourceImpl(
                kpiApiService,
                supportedPolicyApiService,
                offeringService
        );

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                uiResource,
                useCaseResource
        ));
    }
}
