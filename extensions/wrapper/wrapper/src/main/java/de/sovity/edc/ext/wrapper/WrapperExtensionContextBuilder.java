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

import de.sovity.edc.ext.wrapper.api.ui.UiResource;
import de.sovity.edc.ext.wrapper.api.ui.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.ext.wrapper.api.ui.services.ContractAgreementPageService;
import de.sovity.edc.ext.wrapper.api.ui.services.TransferProcessStateService;
import de.sovity.edc.ext.wrapper.api.usecase.UseCaseResource;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.PolicyMappingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import lombok.NoArgsConstructor;
import org.eclipse.edc.api.transformer.DtoTransformerRegistry;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;

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
            ContractDefinitionStore contractDefinitionStore,
            PolicyDefinitionStore policyDefinitionStore,
            PolicyEngine policyEngine,
            TransferProcessStore transferProcessStore,
            ContractAgreementService contractAgreementService,
            ContractNegotiationStore contractNegotiationStore,
            TransferProcessService transferProcessService,
            DtoTransformerRegistry dtoTransformerRegistry
    ) {
        // UI API
        var transferProcessStateService = new TransferProcessStateService();
        var contractAgreementPageCardBuilder = new ContractAgreementPageCardBuilder(
                transferProcessStateService);
        var contractAgreementDataFetcher = new ContractAgreementDataFetcher(
                contractAgreementService,
                contractNegotiationStore,
                transferProcessService
        );
        var contractAgreementApiService = new ContractAgreementPageService(
                contractAgreementDataFetcher,
                contractAgreementPageCardBuilder
        );
        var uiResource = new UiResource(contractAgreementApiService);

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
                contractDefinitionStore, dtoTransformerRegistry, policyMappingService);
        var useCaseResource = new UseCaseResource(kpiApiService, supportedPolicyApiService,
                offeringService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                uiResource,
                useCaseResource
        ));
    }
}
