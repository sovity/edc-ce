/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessage;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessengerRegistry;
import de.sovity.edc.ce.modules.messaging.messenger.impl.HandlerBox;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageRequest;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageResponse;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessengerStatus;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.services.spi.protocol.ProtocolTokenValidator;
import org.eclipse.edc.participant.spi.ParticipantAgent;
import org.eclipse.edc.policy.context.request.spi.RequestCatalogPolicyContext;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static de.sovity.edc.ce.modules.messaging.messenger.controller.SovityMessageController.PATH;

@RequiredArgsConstructor
@Path(PATH)
public class SovityMessageController {

    public static final String PATH = "/sovity/message/generic";

    private final ProtocolTokenValidator protocolTokenValidator;

    private final TypeTransformerRegistry typeTransformerRegistry;
    private final Monitor monitor;
    private final ObjectMapper mapper;

    @Getter
    private final SovityMessengerRegistry handlers;

    @POST
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        SovityMessageRequest request
    ) {

        val validation = validateToken(authorization);
        if (validation.failed()) {
            return Response.status(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                String.join(", ", validation.getFailureMessages())
            ).build();
        }

        val participantAgent = validation.getContent();

        val handler = getHandler(request);
        if (handler == null) {
            val errorAnswer = buildErrorNoHandlerHeader(request);
            return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(errorAnswer).build();
        }

        try {
            val response = processMessage(request, handler, participantAgent);

            return typeTransformerRegistry.transform(response, JsonObject.class)
                .map(it -> Response.ok().type(MediaType.APPLICATION_JSON).entity(it).build())
                .orElseThrow(f -> new EdcException("DSP: Transformation failed: %s".formatted(f.getMessages())));
        } catch (Exception e) {
            monitor.warning("Failed to process message with type " + getMessageType(request), e);
            val errorAnswer = buildErrorHandlerExceptionHeader(request);
            return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(errorAnswer)
                .build();
        }
    }

    private SovityMessageResponse processMessage(
        SovityMessageRequest compacted,
        HandlerBox<Object, Object> handler,
        ParticipantAgent participantAgent
    ) throws JsonProcessingException {
        val bodyStr = compacted.body();
        val parsed = mapper.readValue(bodyStr, handler.clazz());
        val result = handler.handler().apply(participantAgent, parsed);
        val resultBody = mapper.writeValueAsString(result);

        return new SovityMessageResponse(buildOkHeader(handler.clazz()), resultBody);
    }

    private String buildOkHeader(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            String type = ((SovityMessage) constructor.newInstance()).getType();
            JsonObject header = Json.createObjectBuilder()
                .add("status", SovityMessengerStatus.OK.getCode())
                .add("type", type)
                .build();
            return JsonUtils.toJson(header);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new EdcException(e);
        }
    }

    private ServiceResult<ParticipantAgent> validateToken(String authorization) {
        val token = TokenRepresentation.Builder.newInstance().token(authorization).build();
        // request.catalog is a magic string to pretend that we are something else and avoid dealing with the policy evaluation engine
        return protocolTokenValidator.verify(token, RequestCatalogPolicyContext::new);
    }

    private SovityMessageResponse buildErrorNoHandlerHeader(SovityMessageRequest request) {
        val messageType = getMessageType(request);
        val json = Json.createObjectBuilder()
            .add("status", SovityMessengerStatus.NO_HANDLER.getCode())
            .add("message", "No handler for message type " + messageType)
            .build();
        val headerStr = JsonUtils.toJson(json);

        return new SovityMessageResponse(headerStr, "");
    }

    private SovityMessageResponse buildErrorHandlerExceptionHeader(SovityMessageRequest request) {
        val messageType = getMessageType(request);
        val body = request.body();
        val json = Json.createObjectBuilder()
            .add("status", SovityMessengerStatus.HANDLER_EXCEPTION.getCode())
            .add("message", "Error when processing a message with type " + messageType)
            .add(SovityMessengerStatus.HANDLER_EXCEPTION.getCode(), body)
            .build();
        val headerStr = JsonUtils.toJson(json);

        return new SovityMessageResponse(headerStr, "");
    }

    private HandlerBox<Object, Object> getHandler(SovityMessageRequest request) {
        final var messageType = getMessageType(request);
        return handlers.getHandler(messageType);
    }

    private static String getMessageType(SovityMessageRequest request) {
        val headerStr = request.header();
        val header = Json.createReader(new StringReader(headerStr)).readObject();
        return header.getString("type");
    }
}
