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
package org.eclipse.edc.mvd;

import catalogtransfer.transfer.ContractDefinitionProvider;
import org.eclipse.edc.connector.contract.spi.offer.ContractOfferResolver;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

@Provides(ContractDefinitionProvider.class)
public class EventSubscriptionExtension implements ServiceExtension {
    @Inject
    private EventRouter eventRouter;

    @Inject
    private ContractDefinitionStore contractDefinitionStore;

    @Inject
    private PolicyDefinitionStore policyDefinitionStore;

    @Inject
    private ContractDefinitionService contractDefinitionService;

    @Inject
    private ContractOfferResolver contractOfferResolver;

    @Inject
    private AssetIndex assetIndex;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var eventSubscriber = new CustomEventSubscriber(contractDefinitionStore);
        eventRouter.registerSync(eventSubscriber); //asynchronous dispatch - registerSync for synchronous dispatch
        context.registerService(ContractDefinitionProvider.class, eventSubscriber);
    }
}
