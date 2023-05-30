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

package de.sovity.edc.ext.brokerserver.services.refreshing.selfdescription;

import lombok.RequiredArgsConstructor;

/**
 * Fetch Connector Metadata.
 */
@RequiredArgsConstructor
public class ConnectorSelfDescriptionFetcher {
    public ConnectorSelfDescription fetch(String connectorEndpoint) {
        return new ConnectorSelfDescription(
                "Unknown Connector",
                "As of Core EDC Milestone 9 connector self-descriptions are not supported. The connector was successfully crawled, but there is no connector metadata / description available."
        );
    }
}
