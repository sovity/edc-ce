/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.extension.custommessages;

import lombok.val;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static de.sovity.edc.extension.custommessages.SovityExtendedProtocol.SOVITY_EXTENDED_PROTOCOL;

public class SovityMessageDispatcherImpl implements SovityMessageDispatcher {

    private final EdcHttpClient httpClient;

    private final Map<Class<? extends RemoteMessage>, Handlers<?, ?>> handlers = new HashMap<>();

    public SovityMessageDispatcherImpl(EdcHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String protocol() {
        return SOVITY_EXTENDED_PROTOCOL;
    }

    // TODO: @NotNullApi
    @Override
    public <T, M extends RemoteMessage> CompletableFuture<StatusResult<T>> dispatch(@NotNull Class<T> responseType, @NotNull M message) {
        Objects.requireNonNull(message, "Message was null");

        @SuppressWarnings("unchecked")
        val handler = (Handlers<M, T>) handlers.get(message.getClass());

        val request = handler.requestFactory.createRequest(message);
        // TODO: add auth

        return httpClient.executeAsync(request, handler.delegate.handleResponse());
    }

    @Override
    public <M extends RemoteMessage, R> void registerMessage(Class<M> clazz, DspHttpRequestFactory<M> requestFactory, DspHttpDispatcherDelegate<M, R> delegate) {
        handlers.put(clazz, new Handlers(requestFactory, delegate));
    }

    private record Handlers<M extends RemoteMessage, R>(DspHttpRequestFactory<M> requestFactory, DspHttpDispatcherDelegate<M, R> delegate) { }
}
