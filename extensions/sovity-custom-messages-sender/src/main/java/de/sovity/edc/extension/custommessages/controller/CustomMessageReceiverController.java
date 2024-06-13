package de.sovity.edc.extension.custommessages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.MessageHandlers;
import de.sovity.edc.extension.custommessages.impl.MessageHandlersImpl;
import de.sovity.edc.extension.custommessages.impl.SovityMessageRecord;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.error.DspErrorResponse;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.io.StringReader;
import java.net.URL;
import java.util.UUID;

@RequiredArgsConstructor
@Path("/sovity/message")
public class CustomMessageReceiverController {

    private final IdentityService identityService;
    private final String callbackAddress;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final Monitor monitor;
    private final ObjectMapper mapper;

    @Getter
    private MessageHandlers messageHandlers = new MessageHandlersImpl();

    @SneakyThrows // TODO: rm after URL()) is removed
    @POST
    @Path("/generic")
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        // TODO: can I use SovityMessageRecord directly instead of the JsonObject?
        JsonObject jsonObject) {

        val validation = validateToken(authorization);
        if (validation.failed()) {
            // TODO: add test: unauth on failed val
            return Response.status(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                String.join(", ", validation.getFailureMessages())
            ).build();
        }

        // TODO: test token is valid

        val compacted = JsonLdUtils.tryCompact(jsonObject);
        val headerStr = compacted.getString(Prop.SovityMessageExt.HEADER);
        val header = Json.createReader(new StringReader(headerStr)).readObject();

        val messageType = header.getString("type");

        val handler = getHandler(messageType);

        val bodyStr = compacted.getString(Prop.SovityMessageExt.BODY);

        val response = new SovityMessageRecord(
            new URL("https://example.com"), // TODO: the return type doesn't need any URL
            JsonUtils.toJson(noHandlerForMessageType(messageType)),
            bodyStr);

        // TODO: how to ensure compatibility between different version of the messenger plugin?
        //  may have different serializer?
        //  may have different options on the same serializer?

        val parsed = mapper.readValue(bodyStr, handler.clazz());
        //noinspection unchecked
        val result = handler.handler().handle(parsed);
        val resultBody = mapper.writeValueAsString(result);

        // TODO: change the response type to somthing that has no address
        return typeTransformerRegistry.transform(response, JsonObject.class)
            .map(it -> Response.ok().type(MediaType.APPLICATION_JSON).entity(it).build())
            .orElse(failure -> {
                var errorCode = UUID.randomUUID();
                monitor.warning(String.format("Error transforming negotiation, error id %s: %s", errorCode, failure.getFailureDetail()));
                return DspErrorResponse
                    .type(Prop.SovityMessageExt.MESSAGE)
                    .internalServerError();
            });
    }

    private Result<ClaimToken> validateToken(String authorization) {
        val token = TokenRepresentation.Builder.newInstance().token(authorization).build();
        return identityService.verifyJwtToken(token, callbackAddress);
    }

    private JsonObject noHandlerForMessageType(String messageType) {
        return Json.createObjectBuilder()
            .add("status", "error")
            .add("message", "Not handler for message type " + messageType)
            .build();
    }

    private MessageHandlers.Handler<Object, Object> getHandler(String messageType) {
        // TODO: fallback when not present
        val maybeHandler = messageHandlers.getHandler(messageType);
        if (maybeHandler == null) {
            throw new EdcException("Can't find a handler for the message type " + messageType);
        }
        return maybeHandler;
    }
}
