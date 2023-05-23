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

package de.sovity.edc.ext.brokerserver.services.refreshing.sender.message;

import de.sovity.edc.ext.brokerserver.services.refreshing.sender.ExtendedMessageProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record DescriptionRequestMessage(URL connectorEndpoint) implements RemoteMessage {
    @Override
    public String getProtocol() {
        return ExtendedMessageProtocol.IDS_EXTENDED_PROTOCOL;
    }

    @Override
    public String getConnectorAddress() {
        return connectorEndpoint.toString();
    }
}
