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

package de.sovity.edc.extension.messenger.impl;

import lombok.val;
import org.eclipse.edc.core.transform.TypeTransformerRegistryImpl;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.protocol.dsp.serialization.JsonLdRemoteMessageSerializerImpl;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessageEmitterTest {

    private final ObjectMapperFactory mapperFactory = new ObjectMapperFactory();

    @Test
    void emmitValidMessage_whenEmpty_shouldSucceed() throws IOException {
        // arrange
        TypeTransformerRegistry registry = new TypeTransformerRegistryImpl();
        registry.register(new JsonObjectFromSovityMessageRequest());
        JsonLdRemoteMessageSerializer serializer = new JsonLdRemoteMessageSerializerImpl(
            registry,
            mapperFactory.createObjectMapper(),
            new TitaniumJsonLd(mock(Monitor.class))
        );
        val emitter = new MessageEmitter(serializer);

        // act
        val request = emitter.createRequest(new SovityMessageRequest(
            new URL("https://example.com/api"),
            "header",
            "body"
        ));

        // assert
        assertThat(request.url().toString()).isEqualTo("https://example.com/api/sovity/message/generic");
    }
}
