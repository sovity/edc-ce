/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.FetchedCatalogBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;


/**
 * Manual Dependency Injection result
 *
 * @param brokerServerInitializer Startup Logic
 */
public record BrokerServerExtensionContext(
        BrokerServerInitializer brokerServerInitializer,

        // Required for Integration Tests
        ConnectorUpdater connectorUpdater,
        PolicyMapper policyMapper,
        FetchedCatalogBuilder fetchedCatalogBuilder,
        DataOfferRecordUpdater dataOfferRecordUpdater
) {
    /**
     * This is a hack for our tests.
     * <p>
     * Right now we have no good way to access the context from tests.
     */
    public static BrokerServerExtensionContext instance;
}
