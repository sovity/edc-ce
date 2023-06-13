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
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.DataOfferRs;
import de.sovity.edc.ext.brokerserver.dao.utils.MultisetUtils;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
import lombok.RequiredArgsConstructor;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryDataOfferFetcher {
    private final CatalogQuerySortingService catalogQuerySortingService;
    private final CatalogQueryFilterService catalogQueryFilterService;
    private final CatalogQueryContractOfferFetcher catalogQueryContractOfferFetcher;

    /**
     * Query data offers
     *
     * @param fields  query fields
     * @param filter  filter
     * @param sorting sorting
     * @return {@link Field} of {@link DataOfferRs}s
     */
    public Field<List<DataOfferRs>> queryDataOffers(CatalogQueryFields fields, CatalogQueryFilter filter, CatalogPageSortingType sorting) {
        var c = fields.getConnectorTable();
        var d = fields.getDataOfferTable();

        var query = DSL.select(
                        fields.getAssetId().as("assetId"),
                        d.ASSET_PROPERTIES.cast(String.class).as("assetPropertiesJson"),
                        d.CREATED_AT,
                        d.UPDATED_AT,
                        catalogQueryContractOfferFetcher.getContractOffers(fields).as("contractOffers"),
                        c.ENDPOINT.as("connectorEndpoint"),
                        c.ONLINE_STATUS.as("connectorOnlineStatus"),
                        fields.getOfflineSinceOrLastUpdatedAt().as("connectorOfflineSinceOrLastUpdatedAt")
                )
                .from(d)
                .leftJoin(c).on(c.ENDPOINT.eq(d.CONNECTOR_ENDPOINT))
                .where(catalogQueryFilterService.filter(fields, filter))
                .orderBy(catalogQuerySortingService.getOrderBy(fields, sorting));

        return MultisetUtils.multiset(query, DataOfferRs.class);
    }
}
