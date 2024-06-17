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

package de.sovity.edc.extension.messenger;

import de.sovity.edc.extension.messenger.impl.Handler;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The component where to register message handlers when using the {@link SovityMessenger}.
 */
public class SovityMessengerRegistry {

    private final Map<String, Handler<?, ?>> handlers = new HashMap<>();

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
        register(incomingMessage, type, handler);
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
        register(incomingSignal, type, in -> {
            handler.accept(in);
            return null;
        });
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #register(Class, Function)} for type safety.
     */
    public <IN extends SovityMessage, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }
        handlers.put(type, new Handler<>(clazz, handler));
    }

    /**
     * Use this constructor only if your message can't have a default constructor. Otherwise, prefer using
     * {@link #registerSignal(Class, Consumer)} for type safety.
     */
    public <IN extends SovityMessage, OUT> void registerSignal(Class<IN> clazz, String type, Function<IN, OUT> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("A handler is already registered for " + type);
        }
        handlers.put(type, new Handler<>(clazz, handler));
    }

    /**
     * Retrieve a handler for the specified message type.
     *
     * @param type A unique ID to identify a message type.
     * @return The function to process this message type.
     */
    @SuppressWarnings("unchecked")
    public Handler<Object, Object> getHandler(String type) {
        return (Handler<Object, Object>) handlers.get(type);
    }

    private static <IN extends SovityMessage> String getTypeViaIntrospection(Class<IN> incomingMessage) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        val defaultConstructor = incomingMessage.getConstructor();
        defaultConstructor.setAccessible(true);
        val type = defaultConstructor.newInstance().getType();
        return type;
    }
}
