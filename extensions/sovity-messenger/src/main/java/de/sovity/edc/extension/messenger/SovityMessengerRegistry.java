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

package de.sovity.edc.extension.messenger;

import de.sovity.edc.extension.messenger.impl.HandlerBox;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.spi.iam.ClaimToken;

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
     * @param handler         The function to process this type of message.
     * @param <IN>            Incoming message type.
     * @param <OUT>           Outgoing message type you send with {@link SovityMessenger#send(Class, String, SovityMessage)}.
     */
    @SneakyThrows
    public <IN extends SovityMessage, OUT> void register(Class<IN> incomingMessage, Function<IN, OUT> handler) {
        val type = getTypeViaIntrospection(incomingMessage);
        registerIfNotExists(incomingMessage, type, (claims, in) -> handler.apply(in));
    }

    @SneakyThrows
    public <IN extends SovityMessage, OUT> void register(Class<IN> incomingMessage, BiFunction<ClaimToken, IN, OUT> handler) {
        val type = getTypeViaIntrospection(incomingMessage);
        registerIfNotExists(incomingMessage, type, handler);
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #register(Class, Function)} for type safety.
     */
    public <IN extends SovityMessage, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler) {
        registerIfNotExists(clazz, type, (BiFunction<ClaimToken, IN, Object>) (claimToken, in) -> handler.apply(in));
    }

    public <IN extends SovityMessage, OUT> void register(Class<IN> clazz, String type, BiFunction<ClaimToken, IN, OUT> handler) {
        registerIfNotExists(clazz, type, handler);
    }

    /**
     * Registers a signal. This is a simplified version of a message where no answer is expected.
     *
     * @param incomingSignal The signal to send.
     * @param handler        Signal processing
     */
    @SneakyThrows
    public <IN extends SovityMessage> void registerSignal(Class<IN> incomingSignal, Consumer<IN> handler) {
        val type = getTypeViaIntrospection(incomingSignal);
        registerIfNotExists(incomingSignal, type, (claims, in) -> {
            handler.accept(in);
            return null;
        });
    }

    @SneakyThrows
    public <IN extends SovityMessage> void registerSignal(Class<IN> incomingSignal, BiConsumer<ClaimToken, IN> handler) {
        val type = getTypeViaIntrospection(incomingSignal);
        registerIfNotExists(incomingSignal, type, (claims, in) -> {
            handler.accept(claims, in);
            return null;
        });
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #registerSignal(Class, Consumer)} for type safety.
     */
    public <IN extends SovityMessage> void registerSignal(Class<IN> clazz, String type, Consumer<IN> handler) {
        registerIfNotExists(clazz, type, (claims, in) -> {
            handler.accept(in);
            return null;
        });
    }

    public <IN extends SovityMessage> void registerSignal(Class<IN> clazz, String type, BiConsumer<ClaimToken, IN> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }
        registerIfNotExists(clazz, type, (claims, in) -> {
            handler.accept(claims, in);
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

    private <IN extends SovityMessage, OUT> void registerIfNotExists(Class<IN> clazz, String type, BiFunction<ClaimToken, IN, OUT> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }

        handlers.put(type, new HandlerBox<>(clazz, handler));
    }
}
