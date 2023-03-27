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

package de.sovity.edc.ext.wrapper.api.example;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(info = @Info(title = "Wrapper Example API", version = "1.0.0"))
@Path("wrapper/example-api")
@Tag(name = "Example", description = "Our EDC API Wrapper POC example endpoint")
@RequiredArgsConstructor
public class ExampleResource {

    private final ExampleApiService exampleApiService;

    @POST
    @Path("example")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Dummy endpoint that showcases how to document with our OpenAPI Code Generation.")
    public ExampleResult exampleEndpoint(ExampleQuery query) {
        return exampleApiService.exampleEndpoint(query);
    }
}
