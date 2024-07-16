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

import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogQuery;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;


/**
 * Provides the endpoints for use-case specific requests.
 */
@Path("wrapper/use-case-api")
@Tag(name = "Use Case", description = "Generic Use Case Application API Endpoints.")
public interface UseCaseResource {

    @GET
    @Path("kpis")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Basic KPIs about the running EDC Connector.")
    KpiResult getKpis();

    @GET
    @Path("supported-policy-functions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "List available functions in policies, prohibitions and obligations.")
    List<String> getSupportedFunctions();

    @POST
    @Path("catalog")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch a connector's data offers")
    List<UiDataOffer> queryCatalog(
        @Valid @NotNull
        CatalogQuery catalogQuery
    );
}
