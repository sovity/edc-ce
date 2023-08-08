/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.testbackend;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.atomic.AtomicReference;

@Path("/test-backend")
public class TestBackendController {

    private final AtomicReference<String> consumedData = new AtomicReference<>();

    @Path("/provide/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String provide(@PathParam("id") String id) {
        return id;
    }

    @Path("/consume")
    @POST
    public void consume(String consumeString) {
        consumedData.set(consumeString);
    }

    @Path("/getConsumedData")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getData() {
        return consumedData.get();
    }
}
