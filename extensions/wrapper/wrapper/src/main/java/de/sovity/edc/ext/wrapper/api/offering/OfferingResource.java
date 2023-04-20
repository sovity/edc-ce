package de.sovity.edc.ext.wrapper.api.offering;

import de.sovity.edc.ext.wrapper.api.offering.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.offering.services.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("wrapper/offering")
@Tag(name = "Offering", description = "EDC UI API Endpoints")
@RequiredArgsConstructor
public class OfferingResource {

    private final OfferingService offeringService;

    @POST
    @Path("contract-offer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates an offering")
    public void createOfferingEndpoint(CreateOfferingDto dto) {
        offeringService.create(dto);
    }
}
