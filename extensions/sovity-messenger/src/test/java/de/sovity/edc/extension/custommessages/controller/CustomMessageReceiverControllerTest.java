package de.sovity.edc.extension.custommessages.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.custommessages.impl.JsonObjectFromGenericSovityMessage;
import de.sovity.edc.extension.custommessages.impl.MessageHandlerRegistryImpl;
import de.sovity.edc.extension.custommessages.impl.ObjectMapperFactory;
import de.sovity.edc.extension.custommessages.impl.SovityMessageRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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

    private TypeTransformerRegistryImpl transformers = new TypeTransformerRegistryImpl();
    private ConsoleMonitor monitor = new ConsoleMonitor();
    private ObjectMapperFactory omf = new ObjectMapperFactory();
    private ObjectMapper objectMapper = omf.createObjectMapper();
    private IdentityService identityService = mock(IdentityService.class);
    private MessageHandlerRegistryImpl handlers = new MessageHandlerRegistryImpl();

    @BeforeEach
    public void beforeEach() {
        transformers = new TypeTransformerRegistryImpl();
        transformers.register(new JsonObjectFromGenericSovityMessage());

        monitor = new ConsoleMonitor();

        omf = new ObjectMapperFactory();
        objectMapper = omf.createObjectMapper();

        handlers = new MessageHandlerRegistryImpl();

        reset(identityService);
        when(identityService.verifyJwtToken(any(), any())).thenReturn(Result.success(ClaimToken.Builder.newInstance().build()));
    }

    @Test
    void canAnswerRequest() throws JsonProcessingException, MalformedURLException {
        // arrange

        val handlers = new MessageHandlerRegistryImpl();

        val controller = new CustomMessageReceiverController(
            identityService,
            "http://example.com/callback",
            transformers,
            monitor,
            objectMapper,
            handlers
        );

        Function<Payload, Answer> handler = payload -> new Answer(String.valueOf(payload.getI()));
        handlers.register("foo", handler);

        val message = new SovityMessageRequest(
            new URL("https://example.com/api"), """
            { "type" : "foo" }
            """,
            objectMapper.writeValueAsString(new Payload(1)));

        // act

        try (val response = controller.post("", JsonUtils.parseJsonObj(objectMapper.writeValueAsString(message)))) {
            // assert
            assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        }

    }

    @Test
    void post_whenNonAuthorized_shouldReturnHttp401() throws MalformedURLException, JsonProcessingException {
        // arrange
        val identityService = mock(IdentityService.class);
        when(identityService.verifyJwtToken(any(), any())).thenReturn(Result.failure("Invalid token"));

        val controller = new CustomMessageReceiverController(
            identityService,
            "http://example.com/callback",
            transformers,
            monitor,
            objectMapper,
            handlers
        );

        val message = new SovityMessageRequest(
            new URL("https://example.com/api"), """
            { "type" : "foo" }
            """,
            objectMapper.writeValueAsString(new Payload(1)));

        // act
        try (val response = controller.post("", JsonUtils.parseJsonObj(objectMapper.writeValueAsString(message)))) {
            // assert
            assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        }
    }
}
