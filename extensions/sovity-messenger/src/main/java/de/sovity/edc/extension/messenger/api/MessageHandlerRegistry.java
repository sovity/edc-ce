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

package de.sovity.edc.extension.messenger.api;

import lombok.val;
import org.eclipse.edc.spi.EdcException;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * The component where to register message handlers when using the {@link SovityMessenger}.
 */
public interface MessageHandlerRegistry {
    /**
     * Register a handler to process a sovity message.
     * Prefer using {@link MessageHandlerRegistry#register(Class, Function, Object[])} for a typesafe, non-string-based registration.
     */
    @SuppressWarnings("unchecked")
    default <IN, OUT> void register(String type, Function<IN, OUT> handler, IN... reified) {
        register(getClassOf(reified), type, handler);
    }

    /**
     * Register a handler to process a sovity message.
     *
     * @param outgoingMessage The outgoing message type to register
     * @param handler         The function to process this type of message.
     * @param reified         Convenience to avoid repeating the incoming message type;
     * @param <IN>            Incoming message type.
     * @param <OUT>           Outgoing message type you send with {@link SovityMessenger#send(Class, String, SovityMessage)}.
     */
    @SuppressWarnings("unchecked")
    default <IN, OUT> void register(Class<? extends SovityMessage> outgoingMessage, Function<IN, OUT> handler, IN... reified) {
        try {
            val defaultConstructor = outgoingMessage.getConstructor();
            defaultConstructor.setAccessible(true);
            val type = defaultConstructor.newInstance().getType();
            register(type, handler, reified);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    <IN, OUT> void register(Class<IN> clazz, String type, Function<IN, OUT> handler);

    <IN, OUT> Handler<IN, OUT> getHandler(String type);

    default <IN, OUT> Handler<IN, OUT> getHandler(Class<? extends SovityMessage> clazz) {
        try {
            val constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            val message = constructor.newInstance();
            return getHandler(message.getType());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new EdcException("Failed to get a handler by class name", e);
        }
    }

    record Handler<IN, OUT>(Class<IN> clazz, Function<IN, OUT> handler) {
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClassOf(T[] array) {
        return (Class<T>) array.getClass().getComponentType();
    }
}
