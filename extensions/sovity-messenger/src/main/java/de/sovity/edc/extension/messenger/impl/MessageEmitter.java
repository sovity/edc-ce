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

import de.sovity.edc.extension.messenger.controller.SovityMessageController;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;

@RequiredArgsConstructor
public class MessageEmitter implements DspHttpRequestFactory<SovityMessageRequest> {

    private final JsonLdRemoteMessageSerializer serializer;

    @Override
    public Request createRequest(SovityMessageRequest message) {
        String serialized = serializer.serialize(message);
        return new Request.Builder()
            .url(message.counterPartyAddress() + SovityMessageController.PATH)
            .post(RequestBody.create(
                serialized,
                MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
            ))
            .build();
    }
}
