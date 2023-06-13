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

package de.sovity.edc.ext.brokerserver.dao.pages.catalog;

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.AvailableFilterValuesQuery;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import lombok.RequiredArgsConstructor;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;

import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryAvailableFilterFetcher {
    private final CatalogQueryFilterService catalogQueryFilterService;

    /**
     * Query available filter values.
     *
     * @param fields                       query fields
     * @param filter                       general filter to narrow results down to
     * @param availableFilterValuesQueries one entry for each filter
     * @return {@link Field} with field[iFilter][iValue]
     */
    public Field<JSON> queryAvailableFilterValues(
            CatalogQueryFields fields,
            CatalogQueryFilter filter,
            List<AvailableFilterValuesQuery> availableFilterValuesQueries
    ) {
        var c = fields.getConnectorTable();
        var d = fields.getDataOfferTable();

        var valuesPerFilterAttribute = availableFilterValuesQueries.stream()
                .map(it -> it.getAttributeValueField(fields))
                .map(PostgresDSL::arrayAggDistinct)
                .toList();

        return DSL.select(DSL.jsonArray(valuesPerFilterAttribute))
                .from(d).leftJoin(c).on(c.ENDPOINT.eq(d.CONNECTOR_ENDPOINT))
                .where(catalogQueryFilterService.filter(fields, filter))
                .asField();
    }
}
