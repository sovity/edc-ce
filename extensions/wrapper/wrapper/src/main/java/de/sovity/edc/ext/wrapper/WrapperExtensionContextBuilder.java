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

import de.sovity.edc.ext.wrapper.implementation.example.ExampleApiService;
import de.sovity.edc.ext.wrapper.implementation.example.ExampleResourceImpl;
import de.sovity.edc.ext.wrapper.implementation.example.services.IdsEndpointService;
import de.sovity.edc.ext.wrapper.implementation.usecase.KpiApiService;
import de.sovity.edc.ext.wrapper.implementation.usecase.KpiResourceImpl;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
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
            PolicyDefinitionStore policyDefinitionStore,
            ContractDefinitionStore contractDefinitionStore,
            TransferProcessStore transferProcessStore,
            Config config
    ) {
        // Example API
        var idsEndpointService = new IdsEndpointService(config);
        var exampleApiService = new ExampleApiService(idsEndpointService);
        var exampleResource = new ExampleResourceImpl(exampleApiService);

        // Use Case API
        var kpiApiService = new KpiApiService(
                assetIndex,
                policyDefinitionStore,
                contractDefinitionStore,
                transferProcessStore
        );
        var kpiResource = new KpiResourceImpl(kpiApiService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                exampleResource,
                kpiResource
        ));
    }
}
