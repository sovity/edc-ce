package de.sovity.edc.extension.custommessages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import de.sovity.edc.extension.custommessages.api.SovityMessageApi;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
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

    @SneakyThrows // TODO: rm after URL()) is removed
    @POST
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
        if (handler == null) {
            // TODO: change status for standard message with header status and error description
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        val bodyStr = compacted.getString(Prop.SovityMessageExt.BODY);

        // TODO: how to ensure compatibility between different version of the messenger plugin?
        //  may have different serializer?
        //  may have different options on the same serializer?

        val parsed = mapper.readValue(bodyStr, handler.clazz());
        val result = handler.handler().handle(parsed);
        val resultBody = mapper.writeValueAsString(result);

        val response = new SovityMessageRecord(
            new URL("https://example.com"), // TODO: the return type doesn't need any URL
            buildOkHeader(handler.clazz()),
            resultBody);

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
