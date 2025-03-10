/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import org.eclipse.edc.protocol.dsp.http.spi.dispatcher.response.DspHttpResponseBodyExtractor;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;

@RequiredArgsConstructor
public class SovityMessageRequestBodyExtractor implements DspHttpResponseBodyExtractor<SovityMessageRequest> {

    private final ObjectMapper mapper;

    @Override
    public SovityMessageRequest extractBody(ResponseBody responseBody) {
        try {
            if (responseBody == null) {
                return null;
            }
            String content = responseBody.string();
            return mapper.readValue(content, SovityMessageRequest.class);
        } catch (IOException e) {
            throw new EdcException(e);
        }
    }
}
