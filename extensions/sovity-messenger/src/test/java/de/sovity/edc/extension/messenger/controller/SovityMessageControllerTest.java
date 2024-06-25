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

package de.sovity.edc.extension.messenger.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageResponse;
import de.sovity.edc.extension.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import jakarta.ws.rs.core.Response;
import lombok.val;
import org.eclipse.edc.core.transform.TypeTransformerRegistryImpl;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class SovityMessageControllerTest {

    private TypeTransformerRegistryImpl transformers = new TypeTransformerRegistryImpl();
    private ConsoleMonitor monitor = new ConsoleMonitor();
    private ObjectMapperFactory omf = new ObjectMapperFactory();
    private ObjectMapper objectMapper = omf.createObjectMapper();
    private IdentityService identityService = mock(IdentityService.class);
    private SovityMessengerRegistry handlers = new SovityMessengerRegistry();

    @BeforeEach
    public void beforeEach() {
        transformers = new TypeTransformerRegistryImpl();
        transformers.register(new JsonObjectFromSovityMessageRequest());
        transformers.register(new JsonObjectFromSovityMessageResponse());

        monitor = new ConsoleMonitor();

        omf = new ObjectMapperFactory();
        objectMapper = omf.createObjectMapper();

        handlers = new SovityMessengerRegistry();

        reset(identityService);
        when(identityService.verifyJwtToken(any(), any())).thenReturn(Result.success(ClaimToken.Builder.newInstance().build()));
    }

    @Test
    void canAnswerRequest() throws JsonProcessingException, MalformedURLException {
        // arrange

        val handlers = new SovityMessengerRegistry();

        val controller = new SovityMessageController(
            identityService,
            "http://example.com/callback",
            transformers,
            monitor,
            objectMapper,
            handlers
        );

        Function<Payload, Answer> handler = payload -> new Answer(String.valueOf(payload.getInteger()));
        handlers.register(Payload.class, "foo", handler);

        val message = new SovityMessageRequest(
            new URL("https://example.com/api"),
            """
                { "type" : "foo" }
                """,
            objectMapper.writeValueAsString(new Payload(1)));

        // act

        try (val response = controller.post("", message)) {
            // assert
            assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        }

    }

    @Test
    void post_whenNonAuthorized_shouldReturnHttp401() throws MalformedURLException, JsonProcessingException {
        // arrange
        val identityService = mock(IdentityService.class);
        when(identityService.verifyJwtToken(any(), any())).thenReturn(Result.failure("Invalid token"));

        val controller = new SovityMessageController(identityService, "http://example.com/callback", transformers, monitor, objectMapper, handlers);

        val message = new SovityMessageRequest(
            new URL("https://example.com/api"),
            """
                { "type" : "foo" }
                """,
            objectMapper.writeValueAsString(new Payload(1)));

        // act
        try (val response = controller.post("", message)) {
            // assert
            assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        }
    }

}
