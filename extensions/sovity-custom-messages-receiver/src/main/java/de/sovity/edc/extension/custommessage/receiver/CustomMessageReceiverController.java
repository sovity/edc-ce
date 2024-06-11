package de.sovity.edc.extension.custommessage.receiver;

import de.sovity.edc.utils.jsonld.JsonLdUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.iam.IdentityService;

@RequiredArgsConstructor
@Path("/sovity/message")
public class CustomMessageReceiverController {

    private final IdentityService identityService;

    @POST
    @Path("/echo")
    public Response post(
        @HeaderParam(HttpHeaders.AUTHORIZATION) String authentication,
        JsonObject jsonObject) {

        val compacted = JsonLdUtils.tryCompact(jsonObject);
        val content = compacted.getString("http://example.com/content");

        val response = Json.createObjectBuilder()
            .add("http://example.com/content", "echo " + content)
            .build();

        val expanded = JsonLdUtils.expandKeysOnly(response);
        return Response.ok(expanded, MediaType.APPLICATION_JSON_TYPE).build();
    }

}
