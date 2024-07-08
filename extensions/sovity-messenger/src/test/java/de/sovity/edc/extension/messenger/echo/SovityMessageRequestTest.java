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
 */

package de.sovity.edc.extension.messenger.echo;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.sovity.edc.extension.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.net.MalformedURLException;
import java.net.URL;

class SovityMessageRequestTest {


    @Test
    void canSerialize() throws MalformedURLException, JsonProcessingException, JSONException {
        // arrange
        val message = new SovityMessageRequest(
            new URL("https://example.com"),
            "{\"type\":\"foo\"}",
            "body content"
        );

        val mapper = new ObjectMapperFactory().createObjectMapper();

        // act
        val serialized = mapper.writeValueAsString(message);

        // assert
        JSONAssert.assertEquals(
            """
                {
                  "https://semantic.sovity.io/message/generic/header": "{\\"type\\":\\"foo\\"}",
                  "https://semantic.sovity.io/message/generic/body": "body content"
                }
                """,
            serialized,
            JSONCompareMode.STRICT
        );
    }
}
