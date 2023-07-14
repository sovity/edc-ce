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
import de.sovity.edc.ext.wrapper.api.usecase.services.ConsumptionService;
import de.sovity.edc.ext.wrapper.api.usecase.services.ContractNegotiationConsumptionListener;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.PolicyMappingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.ContractAgreementToContractAgreementDtoTransformer;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.ContractNegotiationToContractNegotiationOutputDtoTransformer;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.DataRequestToDataRequestDtoTransformer;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.PermissionToPermissionDtoTransformer;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.PolicyToPolicyDtoTransformer;
import de.sovity.edc.ext.wrapper.api.usecase.transformer.TransferProcessToTransferProcessOutputDtoTransformer;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.observe.ContractNegotiationObservable;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
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
            ContractDefinitionStore contractDefinitionStore,
            PolicyDefinitionStore policyDefinitionStore,
            PolicyEngine policyEngine,
            TransferProcessStore transferProcessStore,
            ContractAgreementService contractAgreementService,
            ContractNegotiationStore contractNegotiationStore,
            ContractNegotiationService negotiationService,
            TransferProcessService transferProcessService,
            TypeTransformerRegistry transformerRegistry,
            ContractNegotiationObservable negotiationObservable
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
                contractDefinitionStore, policyMappingService);

        transformerRegistry.register(new PermissionToPermissionDtoTransformer());
        transformerRegistry.register(new PolicyToPolicyDtoTransformer());
        transformerRegistry.register(new ContractAgreementToContractAgreementDtoTransformer());
        transformerRegistry.register(new ContractNegotiationToContractNegotiationOutputDtoTransformer());
        transformerRegistry.register(new DataRequestToDataRequestDtoTransformer());
        transformerRegistry.register(new TransferProcessToTransferProcessOutputDtoTransformer());
        var consumptionService = new ConsumptionService(negotiationService, transferProcessService,
                contractNegotiationStore, transferProcessStore, transformerRegistry);

        negotiationObservable.registerListener(new ContractNegotiationConsumptionListener(consumptionService));

        var useCaseResource = new UseCaseResource(kpiApiService, supportedPolicyApiService,
                offeringService, consumptionService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                uiResource,
                useCaseResource
        ));
    }
}
