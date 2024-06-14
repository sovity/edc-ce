package de.sovity.edc.extension.custommessages.impl;

import de.sovity.edc.extension.custommessages.dto.Addition;
import de.sovity.edc.extension.custommessages.dto.Answer;
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
