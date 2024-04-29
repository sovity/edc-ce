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

package de.sovity.edc.ext.brokerserver.services.refreshing;

import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventErrorMessage;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateFailureWriter {
    private final BrokerEventLogger brokerEventLogger;
    private final Monitor monitor;

    public void handleConnectorOffline(DSLContext dsl, ConnectorRecord connector, Throwable e) {
        // Log Status Change and set status to offline if necessary
        if (connector.getOnlineStatus() == ConnectorOnlineStatus.ONLINE || connector.getLastRefreshAttemptAt() == null) {
            monitor.info("Connector is offline: " + connector.getEndpoint(), e);
            brokerEventLogger.logConnectorOffline(dsl, connector.getEndpoint(), getFailureMessage(e));
            connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        }

        connector.setLastRefreshAttemptAt(OffsetDateTime.now());
        connector.update();
    }

    public BrokerEventErrorMessage getFailureMessage(Throwable e) {
        return BrokerEventErrorMessage.ofStackTrace("Unexpected exception during connector update.", e);
    }
}
