package de.sovity.edc.ext.wrapper.api.offering;

import de.sovity.edc.ext.wrapper.api.offering.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.offering.services.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;

/**
 * Provides the endpoints for managing offerings.
 *
 * @author Ronja Quensel (ronja.quensel@isst.fraunhofer.de)
 */
@Path("wrapper/offering")
@Tag(name = "Offering", description = "EDC Contract Offering API Endpoints")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class OfferingResource {

    /** Service for managing offerings. */
    private final OfferingService offeringService;

    /**
     * Creates a new offering consisting of asset, policy definition and contract definition.
     *
     * @param dto contains all required information for the offering.
     * @return a 204 response, if creating the offering was successful.
     */
    @POST
    @Path("contract-offer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates an offering")
    public Response createOfferingEndpoint(CreateOfferingDto dto) {
        if (dto == null) {
            String error = "No CreateOfferingDto provided";
            return Response.status(Status.BAD_REQUEST).entity(error).build();
        }

        offeringService.create(dto);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
