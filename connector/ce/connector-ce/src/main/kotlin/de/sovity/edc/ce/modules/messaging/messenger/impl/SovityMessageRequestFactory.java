/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import de.sovity.edc.ce.modules.messaging.messenger.controller.SovityMessageController;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.protocol.dsp.http.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.protocol.dsp.http.spi.serialization.JsonLdRemoteMessageSerializer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class SovityMessageRequestFactory implements DspHttpRequestFactory<SovityMessageRequest> {

    private final JsonLdRemoteMessageSerializer serializer;

    @Override
    public Request createRequest(SovityMessageRequest message) {
        val serialized = serializer.serialize(message);
        return new Request.Builder()
            .url(message.counterPartyAddress() + SovityMessageController.PATH)
            .post(RequestBody.create(
                serialized,
                MediaType.get(APPLICATION_JSON)
            ))
            .header("Content-Type", APPLICATION_JSON)
            .build();
    }
}
