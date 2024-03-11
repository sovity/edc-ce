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

package de.sovity.edc.extension.testbackendcontroller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.util.concurrent.atomic.AtomicReference;

@Path("/test-backend")
public class TestBackendController {
    private final AtomicReference<String> stringValue = new AtomicReference<>("");

    @GET
    @Path("/data-sink/spy")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataSinkValue() {
        return stringValue.get();
    }

    @PUT
    @Path("/data-sink")
    public void setDataSinkValue(String incomingData) {
        stringValue.set(incomingData);
    }

    @GET
    @Path("/data-source")
    @Produces(MediaType.APPLICATION_JSON)
    public String echoForDataSource(@QueryParam("data") String message) {
        return message;
    }

    @GET
    @Path("/data-source/echo-query-params")
    @Produces(MediaType.APPLICATION_JSON)
    public String echoDataSourceQueryParams(@Context UriInfo uriInfo) {
        return uriInfo.getRequestUri().getQuery();
    }
}
