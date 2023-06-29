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

import de.sovity.edc.ext.brokerserver.api.BrokerServerResource;
import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;


/**
 * Manual Dependency Injection result
 *
 * @param brokerServerResource    REST Resource with API Endpoint implementations
 * @param brokerServerInitializer Startup Logic
 */
public record BrokerServerExtensionContext(
        BrokerServerResource brokerServerResource,
        BrokerServerInitializer brokerServerInitializer,

        // Required for Integration Tests
        ConnectorUpdater connectorUpdater,
        ConnectorCreator connectorCreator
) {
    /**
     * This is a hack for our tests.
     * <p>
     * Right now we have no good way to access the context from tests.
     */
    public static BrokerServerExtensionContext instance;
}
