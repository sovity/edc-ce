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
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogPageRs;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.PageQuery;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryService {
    private final CatalogQueryDataOfferFetcher catalogQueryDataOfferFetcher;
    private final CatalogQueryAvailableFilterFetcher catalogQueryAvailableFilterFetcher;

    /**
     * Query all data required for the catalog page
     *
     * @param dsl                         transaction
     * @param filter                      filter
     * @param sorting                     sorting
     * @param pageQuery                   pagination
     * @param availableFilterValueQueries available filter value queries
     * @return {@link CatalogPageRs}
     */
    public CatalogPageRs queryCatalogPage(
            DSLContext dsl,
            CatalogQueryFilter filter,
            CatalogPageSortingType sorting,
            PageQuery pageQuery,
            List<AvailableFilterValuesQuery> availableFilterValueQueries
    ) {
        var fields = new CatalogQueryFields(Tables.CONNECTOR, Tables.DATA_OFFER);

        var availableFilterValues = catalogQueryAvailableFilterFetcher
                .queryAvailableFilterValues(fields, filter, availableFilterValueQueries);

        var dataOffers = catalogQueryDataOfferFetcher.queryDataOffers(fields, filter, sorting, pageQuery);

        var numTotalDataOffers = catalogQueryDataOfferFetcher.queryNumDataOffers(fields, filter);

        return dsl.select(
                dataOffers.as("dataOffers"),
                availableFilterValues.as("availableFilterValues"),
                numTotalDataOffers.as("numTotalDataOffers")
        ).fetchOneInto(CatalogPageRs.class);
    }

}
