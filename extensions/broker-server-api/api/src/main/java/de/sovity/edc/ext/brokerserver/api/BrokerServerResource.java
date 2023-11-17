/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.api;

import de.sovity.edc.ext.brokerserver.api.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageResult;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferCountResult;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("wrapper/broker")
@Tag(name = "Broker Server", description = "Broker Server API Endpoints. Requires the Broker Server Extension")
public interface BrokerServerResource {

    @POST
    @Path("catalog-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query the Broker's Catalog of Data Offers")
    CatalogPageResult catalogPage(CatalogPageQuery query);

    @POST
    @Path("connector-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query the List of Known Connectors")
    ConnectorPageResult connectorPage(ConnectorPageQuery query);

    @POST
    @Path("data-offer-detail-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query a Data Offer's Detail Page")
    DataOfferDetailPageResult dataOfferDetailPage(DataOfferDetailPageQuery query);

    @POST
    @Path("connector-detail-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query a known Connector's Detail Page")
    ConnectorDetailPageResult connectorDetailPage(ConnectorDetailPageQuery query);

    @PUT
    @Path("connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Add unknown Connectors to the Broker Server")
    void addConnectors(List<String> endpoints, @QueryParam("adminApiKey") String adminApiKey);

    @DELETE
    @Path("connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete known Connectors from the Broker Server")
    void deleteConnectors(List<String> endpoints, @QueryParam("adminApiKey") String adminApiKey);

    @POST
    @Path("authority-portal-api/data-offer-counts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query the amount of public Data Offers by provided Connector URLs")
    DataOfferCountResult dataOfferCount(List<String> endpoints);
}
