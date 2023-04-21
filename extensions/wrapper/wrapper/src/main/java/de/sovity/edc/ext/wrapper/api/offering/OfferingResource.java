package de.sovity.edc.ext.wrapper.api.offering;

import de.sovity.edc.ext.wrapper.api.offering.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.offering.services.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("wrapper/offering")
@Tag(name = "Offering", description = "EDC UI API Endpoints")
@RequiredArgsConstructor
public class OfferingResource {

    private final OfferingService offeringService;

    @POST
    @Path("contract-offer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates an offering")
    public void createOfferingEndpoint(@Valid CreateOfferingDto dto) {
        offeringService.create(dto);
    }
}
