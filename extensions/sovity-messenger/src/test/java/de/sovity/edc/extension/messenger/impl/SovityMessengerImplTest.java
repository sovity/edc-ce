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

import de.sovity.edc.extension.messenger.dto.Addition;
import de.sovity.edc.extension.messenger.dto.Answer;
import lombok.val;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.response.StatusResult;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SovityMessengerImplTest {
    @Test
    void send_whenNoHandler_shouldThrowSovityMessengerException() throws MalformedURLException {
        // arrange
        val registry = mock(RemoteMessageDispatcherRegistry.class);
        CompletableFuture<StatusResult<Object>> future = CompletableFuture.completedFuture(
            StatusResult.success(
                new SovityMessageRequest(
                    new URL("https://example.com/api/dsp"),
                    """
                        {
                            "status": "no_handler",
                            "message": "No handler for foo"
                        }
                        """,
                    null)));

        when(registry.dispatch(any(), any())).thenReturn(future);
        val messenger = new SovityMessengerImpl(registry, new ObjectMapperFactory().createObjectMapper());
        val answer = messenger.send(Answer.class, "https://example.com/api/dsp", new Addition(1, 2));

        // act
        val exception = assertThrows(ExecutionException.class, answer::get);

        // assert
        assertThat(exception.getCause().getMessage()).isEqualTo("No handler for foo");
    }
}
