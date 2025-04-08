/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageRequest;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessengerStatus;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.response.StatusResult;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * The service to send {@link SovityMessage}s.
 * <br>
 * See /docs/dev/sovity-messenger.md for an overview of the feature.
 */
@RequiredArgsConstructor
public class SovityMessenger {

    private final RemoteMessageDispatcherRegistry registry;

    private final ObjectMapper serializer;

    private final Monitor monitor;

    /**
     * Sends a message to the counterparty address and returns a future result.
     *
     * @param resultType The result's class.
     * @param counterPartyAddress The base DSP URL where to send the message. e.g. https://server:port/api/dsp
     * @param counterPartyId The Participant ID of the counterparty EDC. e.g. BPNL1234XX.C1234XX
     * @param payload The message to send.
     * @param <T> The outgoing message type.
     * @param <R> The incoming message type.
     * @return A future result.
     * @throws SovityMessengerException If a problem related to the message processing happened.
     */
    public <T extends SovityMessage, R extends SovityMessage> CompletableFuture<StatusResult<R>> send(
        Class<R> resultType, String counterPartyAddress, String counterPartyId, T payload) {
        try {
            val message = buildMessage(counterPartyAddress, counterPartyId, payload);
            val future = registry.dispatch(SovityMessageRequest.class, message);
            return future.thenApply(processResponse(resultType, payload));
        } catch (URISyntaxException | MalformedURLException | JsonProcessingException e) {
            throw new EdcException("Failed to build a custom sovity message", e);
        }
    }

    static class Discarded implements SovityMessage {
        @Override
        public String getType() {
            return "de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessengerImpl.Discarded";
        }
    }

    /**
     * Fire-and-forget messaging where you don't care about the response.
     */
    public <T extends SovityMessage> void send(String counterPartyAddress, String counterPartyId, T payload) {
        send(Discarded.class, counterPartyAddress, counterPartyId, payload);
    }

    @NotNull
    private <R extends SovityMessage, T> Function<StatusResult<SovityMessageRequest>, StatusResult<R>> processResponse(
        Class<R> resultType, T payload) {
        return statusResult -> statusResult.map(content -> {
            try {
                val headerStr = content.header();
                val header = JsonUtils.parseJsonObj(headerStr);
                if (header.getString("status").equals(SovityMessengerStatus.OK.getCode())) {
                    val resultBody = content.body();
                    return serializer.readValue(resultBody, resultType);
                } else if (header.getString("status").equals(SovityMessengerStatus.HANDLER_EXCEPTION.getCode())) {
                    throw new SovityMessengerException(
                        header.getString("message"),
                        header.getString(SovityMessengerStatus.HANDLER_EXCEPTION.getCode(), "No outgoing body."),
                        payload);
                } else {
                    monitor.info(header.getString("message", "No message in the response header."));
                    throw new SovityMessengerException(header.getString("message"));
                }
            } catch (JsonProcessingException e) {
                throw new EdcException(e);
            }
        });
    }

    @NotNull
    private <T extends SovityMessage> SovityMessageRequest buildMessage(String counterPartyAddress, String counterPartyId, T payload)
        throws MalformedURLException, URISyntaxException, JsonProcessingException {
        val url = new URI(counterPartyAddress).toURL();
        val header1 = Json.createObjectBuilder()
            .add("type", payload.getType())
            .build();
        val header = JsonUtils.toJson(header1);
        val serialized = serializer.writeValueAsString(payload);
        return new SovityMessageRequest(url, counterPartyId, header, serialized);
    }

}
