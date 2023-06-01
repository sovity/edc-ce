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

import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("wrapper/use-case-api")
@Tag(name = "Use Case", description = "Generic Use Case Application API Endpoints.")
@RequiredArgsConstructor
public class UseCaseResource {
    private final KpiApiService kpiApiService;
    private final SupportedPolicyApiService supportedPolicyApiService;

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
