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
