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

import de.sovity.edc.ext.wrapper.api.example.ExampleApiService;
import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import de.sovity.edc.ext.wrapper.api.example.services.IdsEndpointService;
import de.sovity.edc.ext.wrapper.api.usecase.ContractAgreementPageService;
import de.sovity.edc.ext.wrapper.api.usecase.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.KpiResource;
import de.sovity.edc.ext.wrapper.api.usecase.SupportedPolicyApiService;
import de.sovity.edc.ext.wrapper.api.usecase.SupportedPolicyResource;
import de.sovity.edc.ext.wrapper.api.usecase.UiPageResource;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;


/**
 * Manual Dependency Injection.
 * <p>
 * We want to develop as Java Backend Development is done, but we have
 * no CDI / DI Framework to rely on.
 * <p>
 * EDC {@link Inject} only works in {@link WrapperExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class WrapperExtensionContextBuilder {
    public static WrapperExtensionContext buildContext(
            AssetIndex assetIndex,
            Config config,
            ContractDefinitionStore contractDefinitionStore,
            PolicyDefinitionStore policyDefinitionStore,
            PolicyEngine policyEngine,
            TransferProcessStore transferProcessStore,
            ContractAgreementService contractAgreementService,
            ContractNegotiationStore contractNegotiationStore,
            TransferProcessService transferProcessService
    ) {
        // Example API
        var idsEndpointService = new IdsEndpointService(config);
        var exampleApiService = new ExampleApiService(idsEndpointService);
        var exampleResource = new ExampleResource(exampleApiService);

        // Use Case API
        var kpiApiService = new KpiApiService(
                assetIndex,
                policyDefinitionStore,
                contractDefinitionStore,
                transferProcessStore,
                contractAgreementService
        );
        var kpiResource = new KpiResource(kpiApiService);
        var supportedPolicyApiService = new SupportedPolicyApiService(policyEngine);
        var supportedPolicyResource = new SupportedPolicyResource(supportedPolicyApiService);
        var contractAgreementApiService = new ContractAgreementPageService(assetIndex, contractAgreementService, contractNegotiationStore, transferProcessService);
        var contractAgreementResource = new UiPageResource(contractAgreementApiService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                exampleResource,
                kpiResource,
                supportedPolicyResource,
                contractAgreementResource
        ));
    }
}
