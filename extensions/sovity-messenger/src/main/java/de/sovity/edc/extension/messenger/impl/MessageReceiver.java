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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.Response;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.util.function.Function;

@RequiredArgsConstructor
public class MessageReceiver extends DspHttpDispatcherDelegate<SovityMessageRequest, SovityMessageRequest> {

    private final ObjectMapper mapper;

    @Override
    protected Function<Response, SovityMessageRequest> parseResponse() {
        return res -> {
            try {
                val body = res.body();
                if (body == null) {
                    return null;
                }
                String content = body.string();
                return mapper.readValue(content, SovityMessageRequest.class);
            } catch (IOException e) {
                throw new EdcException(e);
            }
        };
    }
}
