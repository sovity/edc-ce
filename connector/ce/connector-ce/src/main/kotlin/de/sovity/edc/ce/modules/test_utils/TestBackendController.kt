/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.test_utils

import de.sovity.edc.ce.modules.test_utils.model.TestBackendOAuth2TokenResponse
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.UriInfo
import java.util.concurrent.ConcurrentHashMap

@Path("/test-backend")
class TestBackendController {
    private val dataSink = ConcurrentHashMap<String, String>()

    @GET
    @Path("/data-sink/spy")
    @Produces(MediaType.APPLICATION_JSON)
    fun getDataSinkValue(): String =
        getDataSinkValue("default")

    @PUT
    @Path("/data-sink")
    fun setDataSinkValue(incomingData: String) {
        setDataSinkValue("default", incomingData)
    }

    @GET
    @Path("/data-source")
    @Produces(MediaType.APPLICATION_JSON)
    fun echoForDataSource(@QueryParam("data") message: String) =
        message

    @GET
    @Path("/data-source/echo-query-params")
    @Produces(MediaType.APPLICATION_JSON)
    fun echoDataSourceQueryParams(@Context uriInfo: UriInfo): String =
        uriInfo.requestUri.query

    @GET
    @Path("/{testId}/data-sink/spy")
    @Produces(MediaType.APPLICATION_JSON)
    fun getDataSinkValue(@PathParam("testId") testId: String): String =
        dataSink.getOrDefault(testId, "")

    @PUT
    @Path("/{testId}/data-sink")
    fun setDataSinkValue(@PathParam("testId") testId: String, incomingData: String) {
        dataSink[testId] = incomingData
    }

    @POST
    @Path("/oauth2-tests/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun generateToken(
        @FormParam("grant_type") grantType: String?,
        @FormParam("client_id") clientId: String?,
        @FormParam("client_secret") clientSecret: String?
    ): TestBackendOAuth2TokenResponse {
        require(grantType == "client_credentials") { "Only client_credentials supported" }
        require(clientId == "test-client-id") { "Invalid client credentials" }
        require(clientSecret == "test-client-secret") { "Invalid client credentials" }
        return TestBackendOAuth2TokenResponse("test-access-token")
    }
}
