/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.control;

import de.sovity.edc.runtime.simple_di.Service;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Produces({MediaType.APPLICATION_JSON})
@Path("/on-demand-asset-data-source")
@Service
public class OnDemandAssetDataSourceController {

    @GET
    @Path("/{path:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("*/*")
    public Response get(@QueryParam("email") String email, @QueryParam("subject") String subject) {
        return Response.ok("""
            This is not real data.

            This asset is accessible on request.

            The offer you are trying to use only has this placeholder as a dummy endpoint and requires you to take extra steps to access it.

            Please contact the data provider for more information about how to access it.
            """ + "\n\n" + "Email: " + email + "\n" + "Subject: " + subject + "\n"
        ).build();
    }
}
