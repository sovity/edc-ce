package de.sovity.edc.extension.custommessages.controller;

import de.sovity.edc.extension.custommessages.echo.SovityMessageRecord;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.error.DspErrorResponse;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.io.StringReader;
import java.net.URL;
import java.util.UUID;

import static org.eclipse.edc.protocol.dsp.type.DspNegotiationPropertyAndTypeNames.DSPACE_TYPE_CONTRACT_NEGOTIATION_ERROR;

@RequiredArgsConstructor
@Path("/sovity/message")
public class CustomMessageReceiverController {

    private final IdentityService identityService;
    private final String callbackAddress;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final Monitor monitor;

    @SneakyThrows // TODO: rm after URL()) is removed
    @POST
    @Path("/generic")
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        JsonObject jsonObject) {

        val token = TokenRepresentation.Builder.newInstance().token(authorization).build();
        identityService.verifyJwtToken(token, callbackAddress);

        // TODO: test token is valid

        val compacted = JsonLdUtils.tryCompact(jsonObject);
        val headerStr = compacted.getString(Prop.SovityMessageExt.HEADER);
        val header = Json.createReader(new StringReader(headerStr)).readObject();

        val messageType = header.getString("type");

//        val handler = getHandler(messageType);

        val bodyStr = compacted.getString(Prop.SovityMessageExt.BODY);

        val response = new SovityMessageRecord(
            new URL("https://example.com"), // TODO: the return type doesn't need any URL
            JsonUtils.toJson(noHandlerForMessageType(messageType)),
            bodyStr);

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

    private JsonObject noHandlerForMessageType(String messageType) {
        return Json.createObjectBuilder()
            .add("status", "error")
            .add("message", "Not handler for message type " + messageType)
            .build();
    }

    private Object getHandler(String messageType) {
        return null;
    }

}
