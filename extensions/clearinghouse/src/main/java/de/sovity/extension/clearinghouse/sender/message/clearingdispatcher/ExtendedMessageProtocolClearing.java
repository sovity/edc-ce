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
package de.sovity.extension.clearinghouse.sender.message.clearingdispatcher;

import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;

public final class ExtendedMessageProtocolClearing {

    private static final String EXTENDED_SUFFIX = "-extended-clearing";
    public static final String IDS_EXTENDED_PROTOCOL_CLEARING = String.format("%s%s", MessageProtocol.IDS_MULTIPART, EXTENDED_SUFFIX);

    private ExtendedMessageProtocolClearing() {
    }
}
