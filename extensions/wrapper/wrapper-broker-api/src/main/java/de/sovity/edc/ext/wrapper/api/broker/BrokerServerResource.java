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

package de.sovity.edc.ext.wrapper.api.broker;

import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorDetailPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.DataOfferDetailPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.DataOfferDetailPageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("wrapper/broker")
@Tag(name = "Broker Server", description = "Broker Server API Endpoints. Requires the Broker Server Extension")
public interface BrokerServerResource {

    @POST
    @Path("catalog-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query indexed Contract Offers")
    CatalogPageResult catalogPage(CatalogPageQuery query);

    @POST
    @Path("connector-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query known Connectors")
    ConnectorPageResult connectorPage(ConnectorPageQuery query);

    @POST
    @Path("data-offer-detail-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query data offer details")
    DataOfferDetailPageResult dataOfferDetailPage(DataOfferDetailPageQuery query);

    @POST
    @Path("connector-detail-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Query known Connectors details")
    ConnectorDetailPageResult connectorDetailPage(ConnectorDetailPageQuery query);
}
