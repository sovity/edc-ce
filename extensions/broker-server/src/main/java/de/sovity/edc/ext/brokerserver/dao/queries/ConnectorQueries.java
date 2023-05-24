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

package de.sovity.edc.ext.brokerserver.dao.queries;

import de.sovity.edc.ext.brokerserver.dao.models.ConnectorPageDbRow;
import de.sovity.edc.ext.brokerserver.dao.queries.utils.SearchUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingType;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Stream;

public class ConnectorQueries {

    public Stream<ConnectorRecord> findAll(DSLContext dslContext) {
        return dslContext.selectFrom(Tables.CONNECTOR).stream();
    }

    public ConnectorRecord findByEndpoint(DSLContext dsl, String endpoint) {
        var c = Tables.CONNECTOR;
        return dsl.selectFrom(c).where(c.ENDPOINT.eq(endpoint)).fetchOne();
    }

    public List<ConnectorPageDbRow> forConnectorPage(DSLContext dsl, String searchQuery, ConnectorPageSortingType sorting) {
        var c = Tables.CONNECTOR;
        var filterBySearchQuery = SearchUtils.simpleSearch(searchQuery, List.of(
                c.TITLE, c.DESCRIPTION, c.ENDPOINT, c.IDS_ID, c.CONNECTOR_ID));
        return dsl.select(c.asterisk(), dataOfferCount(c.ENDPOINT).as("numDataOffers"))
                .from(c)
                .where(filterBySearchQuery)
                .orderBy(sortConnectorPage(c, sorting))
                .fetchInto(ConnectorPageDbRow.class);
    }

    @NotNull
    private List<OrderField<?>> sortConnectorPage(Connector c, ConnectorPageSortingType sorting) {
        var alphabetically = c.TITLE.asc();
        var recentFirst = c.CREATED_AT.desc();
        if (sorting == ConnectorPageSortingType.MOST_RECENT) {
            return List.of(recentFirst, alphabetically);
        }
        return List.of(alphabetically, recentFirst);
    }

    private Field<Long> dataOfferCount(Field<String> endpoint) {
        var d = Tables.DATA_OFFER;
        return DSL.select(DSL.count()).from(d).where(d.CONNECTOR_ENDPOINT.eq(endpoint)).asField();
    }
}
