/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record SovityMessageRequest(
    @JsonIgnore
    URL counterPartyAddress,

    @JsonIgnore
    String counterPartyId,

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

    @JsonIgnore
    @Override
    public String getCounterPartyId() {
        return counterPartyId;
    }
}
