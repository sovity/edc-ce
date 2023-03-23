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

package de.sovity.edc.ext.wrapper.api.ui.kpi;

import de.sovity.edc.ext.wrapper.api.ui.kpi.model.KpiResult;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@OpenAPIDefinition(info = @Info(title = "Wrapper KPI API", version = "1.0.0"))
@Path("/wrapper/ui/kpi")
@Tag(name = "Wrapper KPI API", description = "Wrapper KPI UI API endpoint")
public interface KpiResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Schema(description = "This is the KPI endpoint of the Wrapper UI API.")
    KpiResult kpiEndpoint();
}
