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

package de.sovity.edc.client.examples;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ExampleQuery;
import de.sovity.edc.client.gen.model.ExampleResult;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {
    @Inject
    EdcClient edcClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        return "Backend-fetched KPI Information:%n%s".formatted(edcClient.useCaseApi().kpiEndpoint().toString());
    }

    @POST
    @Path("example")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ExampleResult example(ExampleQuery query) {
        return edcClient.exampleApi().exampleEndpoint(query);
    }
}
