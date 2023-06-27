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

package de.sovity.edc.ext.brokerserver.dao.pages.connector;

import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorRs;
import de.sovity.edc.ext.brokerserver.dao.utils.SearchUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingType;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.util.List;

public class ConnectorPageQueryService {
    public List<ConnectorRs> queryConnectorPage(DSLContext dsl, String searchQuery, ConnectorPageSortingType sorting) {
        var c = Tables.CONNECTOR;
        var filterBySearchQuery = SearchUtils.simpleSearch(searchQuery, List.of(c.ENDPOINT, c.CONNECTOR_ID));
        return dsl.select(c.asterisk(), dataOfferCount(c.ENDPOINT).as("numDataOffers"))
                .from(c)
                .where(filterBySearchQuery)
                .orderBy(sortConnectorPage(c, sorting))
                .fetchInto(ConnectorRs.class);
    }

    public ConnectorRs queryConnectorDetailPage(DSLContext dsl, String connectorEndpoint) {
        var c = Tables.CONNECTOR;
        var filterBySearchQuery = SearchUtils.simpleSearch(connectorEndpoint, List.of(c.ENDPOINT, c.CONNECTOR_ID));

        return dsl.select(c.asterisk(), dataOfferCount(c.ENDPOINT).as("numDataOffers"))
                .from(c)
                .where(filterBySearchQuery)
                .fetchOneInto(ConnectorRs.class);
    }

    @NotNull
    private List<OrderField<?>> sortConnectorPage(Connector c, ConnectorPageSortingType sorting) {
        var alphabetically = c.ENDPOINT.asc();
        var recentFirst = c.CREATED_AT.desc();

        if (sorting == null || sorting == ConnectorPageSortingType.TITLE) {
            return List.of(alphabetically, recentFirst);
        } else if (sorting == ConnectorPageSortingType.MOST_RECENT) {
            return List.of(recentFirst, alphabetically);
        }

        throw new IllegalArgumentException("Unhandled sorting type: " + sorting);
    }

    private Field<Long> dataOfferCount(Field<String> endpoint) {
        var d = Tables.DATA_OFFER;
        return DSL.select(DSL.count()).from(d).where(d.CONNECTOR_ENDPOINT.eq(endpoint)).asField();
    }
}
