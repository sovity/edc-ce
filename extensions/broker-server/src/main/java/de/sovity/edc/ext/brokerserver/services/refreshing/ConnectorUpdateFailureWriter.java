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
import org.eclipse.edc.spi.EdcException;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateFailureWriter {
    private final BrokerEventLogger brokerEventLogger;

    public void handleConnectorOffline(DSLContext dsl, ConnectorRecord connector, Throwable e) {
        // Update Connector
        connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        connector.setLastUpdate(OffsetDateTime.now());
        connector.setOfflineSince(OffsetDateTime.now());
        connector.update();

        // Log Event
        brokerEventLogger.logConnectorUpdateFailure(dsl, connector.getEndpoint(), getFailureMessage(e));
    }

    public BrokerEventErrorMessage getFailureMessage(Throwable e) {
        if (isUnexpectedException(e)) {
            return BrokerEventErrorMessage.ofStackTrace("Unexpected exception during connector update.", e);
        }

        return BrokerEventErrorMessage.ofMessage("Failed updating connector.");
    }

    private boolean isUnexpectedException(Throwable e) {
        return !(e instanceof EdcException);
    }
}
