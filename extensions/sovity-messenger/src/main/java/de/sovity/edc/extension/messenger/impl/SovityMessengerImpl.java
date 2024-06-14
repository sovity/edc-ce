package de.sovity.edc.extension.messenger.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.messenger.api.SovityMessage;
import de.sovity.edc.extension.messenger.api.SovityMessenger;
import de.sovity.edc.extension.messenger.api.SovityMessengerException;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.response.StatusResult;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RequiredArgsConstructor
public class SovityMessengerImpl implements SovityMessenger {

    private final RemoteMessageDispatcherRegistry registry;

    private final ObjectMapper serializer;

    @Override
    public <T extends SovityMessage, R extends SovityMessage>
    CompletableFuture<StatusResult<R>> send(Class<R> resultType, String counterPartyAddress, T payload) {
        try {
            val message = buildMessage(counterPartyAddress, payload);
            val future = registry.dispatch(SovityMessageRequest.class, message);
            return future.thenApply(processResponse(resultType));
        } catch (URISyntaxException | MalformedURLException | JsonProcessingException e) {
            throw new EdcException("Failed to build a custom sovity message", e);
        }
    }

    @NotNull
    private <R extends SovityMessage>
    Function<StatusResult<SovityMessageRequest>, StatusResult<R>> processResponse(Class<R> resultType) {
        return statusResult -> statusResult.map(content -> {
            try {
                val headerStr = content.header();
                val header = JsonUtils.parseJsonObj(headerStr);
                if (header.getString("status").equals(SovityMessengerStatus.OK.getCode())) {
                    val resultBody = content.body();
                    return serializer.readValue(resultBody, resultType);
                } else {
                    throw new SovityMessengerException(header.getString("message"));
                }
            } catch (JsonProcessingException e) {
                throw new EdcException(e);
            }
        });
    }

    @NotNull
    private <T extends SovityMessage>
    SovityMessageRequest buildMessage(String counterPartyAddress, T payload)
        throws MalformedURLException, URISyntaxException, JsonProcessingException {
        val url = new URI(counterPartyAddress).toURL();
        val header1 = Json.createObjectBuilder()
            .add("type", payload.getType())
            .build();
        val header = JsonUtils.toJson(header1);
        val serialized = serializer.writeValueAsString(payload);
        return new SovityMessageRequest(url, header, serialized);
    }

}
