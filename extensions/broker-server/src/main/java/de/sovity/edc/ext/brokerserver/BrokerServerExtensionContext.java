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
import de.sovity.edc.ext.wrapper.api.broker.BrokerServerResource;


/**
 * Manual Dependency Injection result
 *
 * @param brokerServerResource    REST Resource with API Endpoint implementations
 * @param brokerServerInitializer Startup Logic
 */
public record BrokerServerExtensionContext(
        BrokerServerResource brokerServerResource,
        BrokerServerInitializer brokerServerInitializer
) {
}
