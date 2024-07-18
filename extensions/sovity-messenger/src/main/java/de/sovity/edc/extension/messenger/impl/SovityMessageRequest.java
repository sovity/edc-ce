/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.messenger.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record SovityMessageRequest(
    @JsonIgnore
    URL counterPartyAddress,

    @JsonProperty(Prop.SovityMessageExt.HEADER)
    String header,

    @JsonProperty(Prop.SovityMessageExt.BODY)
    String body
) implements RemoteMessage {

    @JsonIgnore
    @Override
    public String getProtocol() {
        return HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP;
    }

    @JsonIgnore
    @Override
    public String getCounterPartyAddress() {
        return counterPartyAddress.toString();
    }
}
