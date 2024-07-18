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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.testbackendcontroller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.util.concurrent.ConcurrentHashMap;

@Path("/test-backend")
public class TestBackendController {

    private final ConcurrentHashMap<String, String> dataSink = new ConcurrentHashMap<>();

    @GET
    @Path("/data-sink/spy")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataSinkValue() {
        return getDataSinkValue("default");
    }

    @PUT
    @Path("/data-sink")
    public void setDataSinkValue(String incomingData) {
        setDataSinkValue("default", incomingData);
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

    @GET
    @Path("/{testId}/data-sink/spy")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataSinkValue(@PathParam("testId") String testId) {
        return dataSink.getOrDefault(testId, "");
    }

    @PUT
    @Path("/{testId}/data-sink")
    public void setDataSinkValue(@PathParam("testId") String testId, String incomingData) {
        dataSink.put(testId, incomingData);
    }
}
