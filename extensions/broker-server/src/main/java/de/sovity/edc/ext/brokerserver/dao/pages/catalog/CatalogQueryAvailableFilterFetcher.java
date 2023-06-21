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

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.utils.CollectionUtils2;
import lombok.RequiredArgsConstructor;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryAvailableFilterFetcher {
    private final CatalogQueryFilterService catalogQueryFilterService;

    /**
     * Query available filter values.
     *
     * @param fields      query fields
     * @param searchQuery search query
     * @param filters     filters (values + filter clauses)
     * @return {@link Field} with field[iFilter][iValue]
     */
    public Field<JSON> queryAvailableFilterValues(
            CatalogQueryFields fields,
            String searchQuery,
            List<CatalogQueryFilter> filters
    ) {
        List<Field<JSON>> resultFields = new ArrayList<>();
        for (int i = 0; i < filters.size(); i++) {
            // When querying a filter's values we apply all filters except for the current filter's values
            var currentFilter = filters.get(i);
            var otherFilters = CollectionUtils2.allElementsExceptForIndex(filters, i);
            var resultField = queryFilterValues(fields, currentFilter, searchQuery, otherFilters);
            resultFields.add(resultField);
        }
        return DSL.select(DSL.jsonArray(resultFields)).asField();
    }

    private Field<JSON> queryFilterValues(
            CatalogQueryFields parentQueryFields,
            CatalogQueryFilter currentFilter,
            String searchQuery,
            List<CatalogQueryFilter> otherFilters
    ) {
        var fields = parentQueryFields.withSuffix("filter_" + currentFilter.name());
        var c = fields.getConnectorTable();
        var d = fields.getDataOfferTable();

        return DSL.select(DSL.arrayAggDistinct(currentFilter.valueQuery().getAttributeValueField(fields)))
                .from(d)
                .leftJoin(c).on(c.ENDPOINT.eq(d.CONNECTOR_ENDPOINT))
                .where(catalogQueryFilterService.filter(fields, searchQuery, otherFilters))
                .asField();
    }
}
