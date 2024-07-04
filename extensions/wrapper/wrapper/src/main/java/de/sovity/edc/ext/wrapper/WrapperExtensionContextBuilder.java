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
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetEditRequestMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.OwnConnectorEndpointService;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpHeaderMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.LiteralMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.ui.UiResourceImpl;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetIdValidator;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.CatalogApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.UiDataOfferBuilder;
import de.sovity.edc.extension.contactcancellation.ContractAgreementTerminationService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractNegotiationUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ParameterizationCompatibilityUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.TransferRequestBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionLiteralMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.CriterionOperatorMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationStateService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractOfferMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DapsConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.MiwConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.OwnConnectorEndpointServiceImpl;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import de.sovity.edc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import de.sovity.edc.ext.wrapper.api.usecase.UseCaseResourceImpl;
import de.sovity.edc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionLiteralMapper;
import de.sovity.edc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionMapper;
import de.sovity.edc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionOperatorMapper;
import de.sovity.edc.ext.wrapper.api.usecase.pages.catalog.UseCaseCatalogApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import de.sovity.edc.extension.contactcancellation.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.extension.contactcancellation.query.TerminateContractQuery;
import de.sovity.edc.extension.db.directaccess.DirectDatabaseAccess;
import de.sovity.edc.extension.messenger.SovityMessenger;
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
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

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
        DirectDatabaseAccess directDatabaseAccess,
        JsonLd jsonLd,
        Monitor monitor,
        ObjectMapper objectMapper,
        PolicyDefinitionService policyDefinitionService,
        PolicyDefinitionStore policyDefinitionStore,
        PolicyEngine policyEngine,
        SovityMessenger sovityMessenger,
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
        var selfDescriptionService = new SelfDescriptionService(config, monitor);
        var ownConnectorEndpointService = new OwnConnectorEndpointServiceImpl(selfDescriptionService);
        var assetMapper = newAssetMapper(typeTransformerRegistry, jsonLd, ownConnectorEndpointService);
        var transferProcessStateService = new TransferProcessStateService();
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
        var parameterizationCompatibilityUtils = new ParameterizationCompatibilityUtils();
        var assetIdValidator = new AssetIdValidator();
        var assetApiService = new AssetApiService(
            assetService,
            assetMapper,
            assetIdValidator,
            selfDescriptionService
        );
        var transferRequestBuilder = new TransferRequestBuilder(
            contractAgreementUtils,
            contractNegotiationUtils,
            edcPropertyUtils,
            typeTransformerRegistry,
            parameterizationCompatibilityUtils
        );
        var contractAgreementTransferApiService = new ContractAgreementTransferApiService(
            transferRequestBuilder,
            transferProcessService
        );
        var agreementDetailsQuery = new ContractAgreementTerminationDetailsQuery(directDatabaseAccess::getDslContext);
        var terminateContractQuery = new TerminateContractQuery(directDatabaseAccess::getDslContext);
        var contractAgreementCancellationService = new ContractAgreementTerminationService(sovityMessenger, agreementDetailsQuery, terminateContractQuery);
        var policyDefinitionApiService = new PolicyDefinitionApiService(
            policyDefinitionService,
            policyMapper
        );
        var dataOfferBuilder = new DspDataOfferBuilder(jsonLd);
        var uiDataOfferBuilder = new UiDataOfferBuilder(assetMapper, policyMapper);
        var dspCatalogService = new DspCatalogService(catalogService, dataOfferBuilder);
        var catalogApiService = new CatalogApiService(
            uiDataOfferBuilder,
            dspCatalogService
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
            policyDefinitionService,
            contractDefinitionService
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
            contractAgreementCancellationService,
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
        var filterExpressionOperatorMapper = new FilterExpressionOperatorMapper();
        var filterExpressionLiteralMapper = new FilterExpressionLiteralMapper();
        var filterExpressionMapper = new FilterExpressionMapper(
            filterExpressionOperatorMapper,
            filterExpressionLiteralMapper
        );

        var kpiApiService = new KpiApiService(
            assetIndex,
            policyDefinitionStore,
            contractDefinitionStore,
            transferProcessStore,
            contractAgreementService,
            transferProcessStateService
        );
        var supportedPolicyApiService = new SupportedPolicyApiService(policyEngine);
        var useCaseCatalogApiService = new UseCaseCatalogApiService(
            uiDataOfferBuilder,
            dspCatalogService,
            filterExpressionMapper
        );
        var useCaseResource = new UseCaseResourceImpl(
            kpiApiService,
            supportedPolicyApiService,
            useCaseCatalogApiService,
            policyDefinitionApiService
        );

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
            uiResource,
            useCaseResource
        ), selfDescriptionService);
    }

    @NotNull
    private static AssetMapper newAssetMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        JsonLd jsonLd,
        OwnConnectorEndpointService ownConnectorEndpointService
    ) {
        var edcPropertyUtils = new EdcPropertyUtils();
        var assetJsonLdUtils = new AssetJsonLdUtils();
        var assetEditRequestMapper = new AssetEditRequestMapper();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var assetJsonLdParser = new AssetJsonLdParser(
            assetJsonLdUtils,
            shortDescriptionBuilder,
            ownConnectorEndpointService
        );
        var httpHeaderMapper = new HttpHeaderMapper();
        var httpDataSourceMapper = new HttpDataSourceMapper(httpHeaderMapper);
        var dataSourceMapper = new DataSourceMapper(
            edcPropertyUtils,
            httpDataSourceMapper
        );
        var assetJsonLdBuilder = new AssetJsonLdBuilder(
            dataSourceMapper,
            assetJsonLdParser,
            assetEditRequestMapper
        );
        return new AssetMapper(
            typeTransformerRegistry,
            assetJsonLdBuilder,
            assetJsonLdParser,
            jsonLd
        );
    }
}
