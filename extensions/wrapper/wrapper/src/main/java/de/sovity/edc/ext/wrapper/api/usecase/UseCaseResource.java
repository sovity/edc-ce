/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumeInputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumeOutputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import de.sovity.edc.ext.wrapper.api.usecase.services.ConsumptionService;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;


/**
 * Provides the endpoints for use-case specific requests.
 *
 * @author Ronja Quensel (ronja.quensel@isst.fraunhofer.de)
 * @author Richard Treier
 * @author Tim Dahlmanns
 */
@Path("wrapper/use-case-api")
@Tag(name = "Use Case", description = "Generic Use Case Application API Endpoints.")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class UseCaseResource {
    private final KpiApiService kpiApiService;
    private final SupportedPolicyApiService supportedPolicyApiService;
    /** Service for managing offerings. */
    private final OfferingService offeringService;
    /** Service for managing data consumption */
    private final ConsumptionService consumptionService;

    /**
     * Creates a new offer consisting of asset, policy definition and contract definition.
     *
     * @param dto {@link CreateOfferingDto} contains all required information for the offer.
     * @return 204: if creating the usecase was successful.
     */
    @POST
    @Path("contract-offer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates an offer")
    public Response createOfferEndpoint(CreateOfferingDto dto) {
        offeringService.create(dto);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Creates a new data consumption.
     *
     * @param dto {@link ConsumeInputDto} contains all required information for a data consumption.
     * @return 201: the consumption ID as {@link String}.
     */
    @POST
    @Path("consume")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a data consumption")
    public Response consume(ConsumeInputDto dto) {
        var consumptionId = consumptionService.startConsumptionProcess(dto);
        var response = Map.of("id", consumptionId);
        return Response.status(Status.CREATED).entity(response).build();
    }

    /**
     * Receive information and progress of an ongoing or finished consumption.
     *
     * @param consumptionId {@link String} consumption ID
     * @return 200: {@link ConsumeOutputDto}
     */
    @GET
    @Path("consumption/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get the progress of a consumption")
    public Response getConsumption(@PathParam("id") String consumptionId) {
        var consumptionDto = consumptionService.getConsumptionProcess(consumptionId);
        if (consumptionDto == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.status(Status.OK).entity(consumptionDto).build();
    }

    @GET
    @Path("kpis")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Basic KPIs about the running EDC Connector.")
    public KpiResult kpiEndpoint() {
        return kpiApiService.kpiEndpoint();
    }

    @GET
    @Path("supported-policy-functions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "List available functions in policies, prohibitions and obligations.")
    public List<String> getSupportedFunctions() {
        return supportedPolicyApiService.getSupportedFunctions();
    }
}
