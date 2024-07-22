/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.placeholderdatasource;

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
@Path("/data-source/placeholder")
public class PlaceholderEndpointController {

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
