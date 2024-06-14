package de.sovity.edc.extension.custommessages.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.custommessages.api.SovityMessageApi;
import de.sovity.edc.extension.custommessages.impl.SovityMessageResponse;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@RequiredArgsConstructor
@Path(SovityMessageApi.PATH)
public class CustomMessageReceiverController {

    private final IdentityService identityService;
    private final String callbackAddress;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final Monitor monitor;
    private final ObjectMapper mapper;

    @Getter
    private final MessageHandlerRegistry handlers;

    @POST
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        // TODO: can I use SovityMessageRecord directly instead of the JsonObject?
        JsonObject jsonObject) {

        val validation = validateToken(authorization);
        if (validation.failed()) {
            return Response.status(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                String.join(", ", validation.getFailureMessages())
            ).build();
        }

        val compacted = JsonLdUtils.tryCompact(jsonObject);

        val messageType = extractMessageType(compacted);

        val handler = getHandler(messageType);
        if (handler == null) {
            // TODO: change status for standard message with header status and error description
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: how to ensure compatibility between different version of the messenger plugin?
        //  may have different serializer?
        //  may have different options on the same serializer?
        val response = processMessage(compacted, handler);

        // TODO: change the response type to somthing that has no address
        return typeTransformerRegistry.transform(response, JsonObject.class)
            .map(it -> Response.ok().type(MediaType.APPLICATION_JSON).entity(it).build())
            .orElse(failure -> {
                var errorCode = UUID.randomUUID();
                monitor.warning(String.format("Error transforming negotiation, error id %s: %s", errorCode, failure.getFailureDetail()));
                return DspErrorResponse
                    .type(Prop.SovityMessageExt.REQUEST)
                    .internalServerError();
            });
    }

    private static String extractMessageType(JsonObject compacted) {
        val headerStr = compacted.getString(Prop.SovityMessageExt.HEADER);
        val header = Json.createReader(new StringReader(headerStr)).readObject();

        val messageType = header.getString("type");
        return messageType;
    }

    private SovityMessageResponse processMessage(JsonObject compacted, MessageHandlerRegistry.Handler<Object, Object> handler) {
        try {

            val bodyStr = compacted.getString(Prop.SovityMessageExt.BODY);
            val parsed = mapper.readValue(bodyStr, handler.clazz());
            val result = handler.handler().apply(parsed);
            val resultBody = mapper.writeValueAsString(result);

            val response = new SovityMessageResponse(
                buildOkHeader(handler.clazz()),
                resultBody);

            return response;
        } catch (JsonProcessingException e) {
            throw new EdcException("Failed to process the message", e);
        }
    }

    private String buildOkHeader(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            String type = ((SovityMessage) constructor.newInstance()).getType();
            JsonObject header = Json.createObjectBuilder()
                .add("status", "ok")
                .add("type", type)
                .build();
            return JsonUtils.toJson(header);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new EdcException(e);
        }
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

    private MessageHandlerRegistry.Handler<Object, Object> getHandler(String messageType) {
        return handlers.getHandler(messageType);
    }
}
