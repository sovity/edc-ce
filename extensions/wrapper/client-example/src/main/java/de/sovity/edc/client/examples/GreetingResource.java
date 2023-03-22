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
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {
    @Inject
    EdcClient edcClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String idsEndpoint() {
        return "Backend-fetched IDS Endpoint: %s".formatted(getIdsEndpoint());
    }

    /**
     * Example method that consumes our EDC Java Client.
     *
     * @return IDS Endpoint URL
     */
    private String getIdsEndpoint() {
        var query = new ExampleQuery("A", List.of("B"));
        var result = edcClient.exampleClient().exampleEndpoint(query);
        return result.getIdsEndpoint();
    }
}