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

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogPageRs;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.PageQuery;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryService {
    private final CatalogQueryDataOfferFetcher catalogQueryDataOfferFetcher;
    private final CatalogQueryAvailableFilterFetcher catalogQueryAvailableFilterFetcher;
    private final BrokerServerSettings brokerServerSettings;

    /**
     * Query all data required for the catalog page
     *
     * @param dsl         transaction
     * @param searchQuery search query
     * @param filters     filters (queries + filter clauses)
     * @param sorting     sorting
     * @param pageQuery   pagination
     * @return {@link CatalogPageRs}
     */
    public CatalogPageRs queryCatalogPage(
            DSLContext dsl,
            String searchQuery,
            List<CatalogQueryFilter> filters,
            CatalogPageSortingType sorting,
            PageQuery pageQuery
    ) {
        var fields = new CatalogQueryFields(
                Tables.CONNECTOR,
                Tables.DATA_OFFER,
                brokerServerSettings.getDataSpaceConfig()
        );

        var availableFilterValues = catalogQueryAvailableFilterFetcher
                .queryAvailableFilterValues(fields, searchQuery, filters);

        var dataOffers = catalogQueryDataOfferFetcher.queryDataOffers(fields, searchQuery, filters, sorting, pageQuery);

        var numTotalDataOffers = catalogQueryDataOfferFetcher.queryNumDataOffers(fields, searchQuery, filters);

        return dsl.select(
                dataOffers.as("dataOffers"),
                availableFilterValues.as("availableFilterValues"),
                numTotalDataOffers.as("numTotalDataOffers")
        ).fetchOneInto(CatalogPageRs.class);
    }

}
