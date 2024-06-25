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
import de.sovity.edc.extension.messenger.SovityMessage;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import de.sovity.edc.extension.messenger.impl.Handler;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.SovityMessageResponse;
import de.sovity.edc.extension.messenger.impl.SovityMessengerStatus;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
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
import org.eclipse.edc.protocol.dsp.api.configuration.error.DspErrorResponse;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static de.sovity.edc.extension.messenger.controller.SovityMessageController.PATH;

@RequiredArgsConstructor
@Path(PATH)
public class SovityMessageController {

    public static final String PATH = "/sovity/message/generic";

    private final IdentityService identityService;
    private final String callbackAddress;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final Monitor monitor;
    private final ObjectMapper mapper;

    @Getter
    private final SovityMessengerRegistry handlers;

    @POST
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        SovityMessageRequest request) {

        val validation = validateToken(authorization);
        if (validation.failed()) {
            return Response.status(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                String.join(", ", validation.getFailureMessages())
            ).build();
        }

        val handler = getHandler(request);
        if (handler == null) {
            val errorAnswer = buildErrorNoHandlerHeader(request);
            return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(errorAnswer).build();
        }

        try {
            val response = processMessage(request, handler);

            return typeTransformerRegistry.transform(response, JsonObject.class)
                .map(it -> Response.ok().type(MediaType.APPLICATION_JSON).entity(it).build())
                .orElse(failure -> {
                    var errorCode = UUID.randomUUID();
                    monitor.warning(String.format("Error transforming " + response.getClass().getSimpleName() + ", error id %s: %s", errorCode, failure.getFailureDetail()));
                    return DspErrorResponse
                        .type(Prop.SovityMessageExt.REQUEST)
                        .internalServerError();
                });
        } catch (Exception e) {
            monitor.warning("Failed to process message with type " + getMessageType(request), e);
            val errorAnswer = buildErrorHandlerExceptionHeader(request);
            return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(errorAnswer)
                .build();
        }
    }

    private SovityMessageResponse processMessage(SovityMessageRequest compacted, Handler<Object, Object> handler) throws JsonProcessingException {
        val bodyStr = compacted.body();
        val parsed = mapper.readValue(bodyStr, handler.clazz());
        val result = handler.handler().apply(parsed);
        val resultBody = mapper.writeValueAsString(result);

        val response = new SovityMessageResponse(
            buildOkHeader(handler.clazz()),
            resultBody);

        return response;
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

    private Result<ClaimToken> validateToken(String authorization) {
        val token = TokenRepresentation.Builder.newInstance().token(authorization).build();
        return identityService.verifyJwtToken(token, callbackAddress);
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

    private Handler<Object, Object> getHandler(SovityMessageRequest request) {
        final var messageType = getMessageType(request);
        return handlers.getHandler(messageType);
    }

    private static String getMessageType(SovityMessageRequest request) {
        val headerStr = request.header();
        val header = Json.createReader(new StringReader(headerStr)).readObject();
        return header.getString("type");
    }
}
