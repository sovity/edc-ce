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
import de.sovity.edc.ext.wrapper.api.ee.EnterpriseEditionResourceImpl;
import de.sovity.edc.ext.wrapper.api.ui.UiResource;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.AssetBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.utils.AssetPropertyMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractDefinitionBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.TransferProcessStateService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.TransferRequestBuilder;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractAgreementUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractDefinitionUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractNegotiationUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import de.sovity.edc.ext.wrapper.api.usecase.UseCaseResource;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.PolicyMappingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

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
            ServiceExtensionContext serviceExtensionContext,
            AssetIndex assetIndex,
            AssetService assetService,
            ContractAgreementService contractAgreementService,
            ContractDefinitionStore contractDefinitionStore,
            ContractNegotiationService contractNegotiationService,
            ContractNegotiationStore contractNegotiationStore,
            ObjectMapper objectMapper,
            PolicyDefinitionStore policyDefinitionStore,
            PolicyEngine policyEngine,
            TransferProcessStore transferProcessStore,
            TransferProcessService transferProcessService,
            ContractDefinitionService contractDefinitionService
    ) {
        // UI API
        var transferProcessStateService = new TransferProcessStateService();
        var contractAgreementPageCardBuilder =
                new ContractAgreementPageCardBuilder(
                        transferProcessStateService);
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
        var contractDefinitionUtils = new ContractDefinitionUtils();
        var contactDefinitionBuilder = new ContractDefinitionBuilder(contractDefinitionUtils);
        var contractDefinitionApiService = new ContractDefinitionApiService(contractDefinitionService, contractDefinitionUtils, contactDefinitionBuilder);
        var transferHistoryPageApiService = new TransferHistoryPageApiService(
                assetService,
                contractAgreementService,
                contractNegotiationStore,
                transferProcessService,
                transferProcessStateService);
        var transferHistoryPageAssetFetcherService = new TransferHistoryPageAssetFetcherService(
                assetService,
                transferProcessService);
        var contractNegotiationUtils = new ContractNegotiationUtils(contractNegotiationService);
        var contractAgreementUtils = new ContractAgreementUtils(contractAgreementService);
        var assetUtils = new AssetPropertyMapper();
        var assetBuilder = new AssetBuilder(assetUtils);
        var assetApiService = new AssetApiService(assetBuilder, assetService, assetUtils);
        var transferRequestBuilder = new TransferRequestBuilder(
                objectMapper,
                contractAgreementUtils,
                contractNegotiationUtils,
                serviceExtensionContext.getConnectorId()
        );
        var contractAgreementTransferApiService = new ContractAgreementTransferApiService(
                transferRequestBuilder,
                transferProcessService
        );
        var uiResource = new UiResource(
                contractAgreementApiService,
                contractAgreementTransferApiService,
                transferHistoryPageApiService,
                transferHistoryPageAssetFetcherService,
                assetApiService,
                contractDefinitionApiService
        );

        // Use Case API
        var kpiApiService = new KpiApiService(
                assetIndex,
                policyDefinitionStore,
                contractDefinitionStore,
                transferProcessStore,
                contractAgreementService
        );
        var supportedPolicyApiService = new SupportedPolicyApiService(policyEngine);
        var policyMappingService = new PolicyMappingService();
        var offeringService = new OfferingService(assetIndex, policyDefinitionStore,
                contractDefinitionStore, policyMappingService);
        var useCaseResource = new UseCaseResource(kpiApiService, supportedPolicyApiService,
                offeringService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                uiResource,
                useCaseResource,
                new EnterpriseEditionResourceImpl()
        ));
    }
}
