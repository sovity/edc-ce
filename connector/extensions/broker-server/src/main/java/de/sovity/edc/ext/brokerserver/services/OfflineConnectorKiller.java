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

package de.sovity.edc.ext.brokerserver.services;

import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class OfflineConnectorKiller {
    private final BrokerServerSettings brokerServerSettings;
    private final ConnectorQueries connectorQueries;
    private final BrokerEventLogger brokerEventLogger;
    private final ConnectorKiller connectorKiller;
    private final ConnectorCleaner connectorClearer;

    public void killIfOfflineTooLong(DSLContext dsl) {
        var killOfflineConnectorsAfter = brokerServerSettings.getKillOfflineConnectorsAfter();
        var toKill = connectorQueries.findAllConnectorsForKilling(dsl, killOfflineConnectorsAfter);

        connectorClearer.removeDataForDeadConnectors(dsl, toKill);
        connectorKiller.killConnectors(dsl, toKill);

        brokerEventLogger.addKilledDueToOfflineTooLongMessages(dsl, toKill);
    }
}
