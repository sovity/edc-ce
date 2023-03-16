package de.sovity.edc.ext.wrapper.api.example;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@OpenAPIDefinition(info = @Info(title = "Wrapper Example API", version = "1.0.0"))
@Path("/wrapper/example")
@Tag(name = "Wrapper Example API", description = "Our EDC API Wrapper POC example endpoint")
public interface ExampleResource {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ExampleResult exampleEndpoint(ExampleQuery query);
}
