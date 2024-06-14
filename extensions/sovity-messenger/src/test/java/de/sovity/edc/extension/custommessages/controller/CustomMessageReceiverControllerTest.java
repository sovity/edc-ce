package de.sovity.edc.extension.custommessages.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.custommessages.impl.JsonObjectFromGenericSovityMessage;
import de.sovity.edc.extension.custommessages.impl.MessageHandlerRegistryImpl;
import de.sovity.edc.extension.custommessages.impl.ObjectMapperFactory;
import de.sovity.edc.extension.custommessages.impl.SovityMessageRecord;
import de.sovity.edc.utils.JsonUtils;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.eclipse.edc.core.transform.TypeTransformerRegistryImpl;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.result.Result;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomMessageReceiverControllerTest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Payload implements SovityMessage {
        @JsonProperty("integer")
        private Integer i;

        @Override
        public String getType() {
            return "payload";
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Answer implements SovityMessage {
        @JsonProperty("string")
        private String s;

        @Override
        public String getType() {
            return "answer";
        }
    }

    @Test
    void canAnswerRequest() throws JsonProcessingException, MalformedURLException {
        // arrange
        val transformers = new TypeTransformerRegistryImpl();
        transformers.register(new JsonObjectFromGenericSovityMessage());
        val monitor = new ConsoleMonitor();
        val omf = new ObjectMapperFactory();
        val objectMapper = omf.createObjectMapper();

        val mock = mock(IdentityService.class);
        when(mock.verifyJwtToken(any(), any())).thenReturn(Result.success(ClaimToken.Builder.newInstance().build()));

        val handlers = new MessageHandlerRegistryImpl();

        val controller = new CustomMessageReceiverController(
            mock,
            "http://example.com/callback",
            transformers,
            monitor,
            objectMapper,
            handlers
        );

        Function<Payload, Answer> handler = payload -> new Answer(String.valueOf(payload.getI()));
        handlers.register("foo", handler);

        // act
        val message = new SovityMessageRecord(
            new URL("https://example.com/api"), """
            { "type" : "foo" }
            """,
            objectMapper.writeValueAsString(new Payload(1)));

        val response = controller.post("", JsonUtils.parseJsonObj(objectMapper.writeValueAsString(message)));

        // assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
