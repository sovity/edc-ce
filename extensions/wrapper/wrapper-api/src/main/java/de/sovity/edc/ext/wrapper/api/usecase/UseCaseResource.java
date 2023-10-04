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

import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;


/**
 * Provides the endpoints for use-case specific requests.
 *
 * @author Ronja Quensel (ronja.quensel@isst.fraunhofer.de)
 * @author Richard Treier
 * @author Tim Dahlmanns
 */
@Path("wrapper/use-case-api")
@Tag(name = "Use Case", description = "Generic Use Case Application API Endpoints.")
public interface UseCaseResource {
    @POST
    @Path("contract-offer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a new data offer, consisting of an asset, a policy definition and a contract definition.")
    void createOfferEndpoint(CreateOfferingDto dto);

    @GET
    @Path("kpis")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Basic KPIs about the running EDC Connector.")
    KpiResult getKpiEndpoint();

    @GET
    @Path("supported-policy-functions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "List available functions in policies, prohibitions and obligations.")
    List<String> getSupportedFunctions();
}
