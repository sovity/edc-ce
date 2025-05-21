/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import de.sovity.edc.ce.modules.messaging.messenger.impl.HandlerBox;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.participant.spi.ParticipantAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The component where to register message handlers when using the {@link SovityMessenger}.
 */
public class SovityMessengerRegistry {

    private final Map<String, HandlerBox<?, ?>> handlers = new HashMap<>();

    /**
     * Register a handler to process a sovity message.
     *
     * @param incomingMessage The incoming message type to register (what was sent). Must have a no-arg constructor.
     * @param handler The function to process this type of message.
     * @param <IN> Incoming message type.
     * @param <OUT> Outgoing message type you send with {@link SovityMessenger#send(Class, String, String, SovityMessage)}.
     */
    @SneakyThrows
    public <IN extends SovityMessage, OUT> void register(Class<IN> incomingMessage, Function<IN, OUT> handler) {
        val type = getTypeViaIntrospection(incomingMessage);
        registerIfNotExists(incomingMessage, type, (participantAgent, in) -> handler.apply(in));
    }

    @SneakyThrows
    public <IN extends SovityMessage, OUT> void register(Class<IN> incomingMessage, BiFunction<ParticipantAgent, IN, OUT> handler) {
        val type = getTypeViaIntrospection(incomingMessage);
        registerIfNotExists(incomingMessage, type, handler);
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #register(Class, Function)} for type safety.
     */
    public <IN extends SovityMessage, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler) {
        registerIfNotExists(clazz, type, (BiFunction<ParticipantAgent, IN, Object>) (claimToken, in) -> handler.apply(in));
    }

    public <IN extends SovityMessage, OUT> void register(Class<IN> clazz, String type, BiFunction<ParticipantAgent, IN, OUT> handler) {
        registerIfNotExists(clazz, type, handler);
    }

    /**
     * Registers a signal. This is a simplified version of a message where no answer is expected.
     *
     * @param incomingSignal The signal to send.
     * @param handler Signal processing
     */
    @SneakyThrows
    public <IN extends SovityMessage> void registerSignal(Class<IN> incomingSignal, Consumer<IN> handler) {
        val type = getTypeViaIntrospection(incomingSignal);
        registerIfNotExists(incomingSignal, type, (participantAgent, in) -> {
            handler.accept(in);
            return null;
        });
    }

    @SneakyThrows
    public <IN extends SovityMessage> void registerSignal(Class<IN> incomingSignal, BiConsumer<ParticipantAgent, IN> handler) {
        val type = getTypeViaIntrospection(incomingSignal);
        registerIfNotExists(incomingSignal, type, (participantAgent, in) -> {
            handler.accept(participantAgent, in);
            return null;
        });
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #registerSignal(Class, Consumer)} for type safety.
     */
    public <IN extends SovityMessage> void registerSignal(Class<IN> clazz, String type, Consumer<IN> handler) {
        registerIfNotExists(clazz, type, (participantAgent, in) -> {
            handler.accept(in);
            return null;
        });
    }

    public <IN extends SovityMessage> void registerSignal(Class<IN> clazz, String type, BiConsumer<ParticipantAgent, IN> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }
        registerIfNotExists(clazz, type, (participantAgent, in) -> {
            handler.accept(participantAgent, in);
            return null;
        });
    }

    /**
     * Retrieve a handler for the specified message type.
     *
     * @param type A unique ID to identify a message type.
     * @return The function to process this message type.
     */
    @SuppressWarnings("unchecked")
    public HandlerBox<Object, Object> getHandler(String type) {
        return (HandlerBox<Object, Object>) handlers.get(type);
    }

    @SneakyThrows
    private static <IN extends SovityMessage> String getTypeViaIntrospection(Class<IN> incomingMessage) {
        val defaultConstructor = incomingMessage.getConstructor();
        defaultConstructor.setAccessible(true);
        val type = defaultConstructor.newInstance().getType();
        return type;
    }

    private <IN extends SovityMessage, OUT> void registerIfNotExists(
        Class<IN> clazz,
        String type,
        BiFunction<ParticipantAgent, IN, OUT> handler
    ) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }

        handlers.put(type, new HandlerBox<>(clazz, handler));
    }
}
