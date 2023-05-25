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

import com.github.t9t.jooq.json.JsonbDSL;
import de.sovity.edc.ext.brokerserver.dao.AssetProperty;
import de.sovity.edc.ext.brokerserver.dao.models.DataOfferContractOfferDbRow;
import de.sovity.edc.ext.brokerserver.dao.models.DataOfferDbRow;
import de.sovity.edc.ext.brokerserver.dao.queries.utils.SearchUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.DataOffer;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.util.List;

public class DataOfferQueries {
    public List<DataOfferDbRow> forCatalogPage(DSLContext dsl, String searchQuery, CatalogPageSortingType sorting) {
        var c = Tables.CONNECTOR;
        var d = Tables.DATA_OFFER;

        // Asset Properties from JSON to be used in sorting / filtering
        var assetId = JsonbDSL.extractPathText(d.ASSET_PROPERTIES, AssetProperty.ASSET_ID);
        var assetTitle = DSL.coalesce(JsonbDSL.extractPathText(d.ASSET_PROPERTIES, AssetProperty.TITLE), assetId);
        var assetDescription = JsonbDSL.extractPathText(d.ASSET_PROPERTIES, AssetProperty.DESCRIPTION);
        var assetKeywords = JsonbDSL.extractPathText(d.ASSET_PROPERTIES, AssetProperty.KEYWORDS);

        // This date should always be non-null
        // It's used in the UI to display the last relevant change date of a connector
        var offlineSinceOrLastUpdatedAt = DSL.coalesce(
                DSL.case_(c.ONLINE_STATUS).when(ConnectorOnlineStatus.OFFLINE, c.OFFLINE_SINCE).else_(c.LAST_UPDATE),
                c.CREATED_AT
        );

        var filterBySearchQuery = SearchUtils.simpleSearch(searchQuery, List.of(
                assetId,
                assetTitle,
                assetDescription,
                assetKeywords,
                c.TITLE,
                c.ENDPOINT
        ));

        return dsl.select(
                        assetId.as("assetId"),
                        c.ENDPOINT.as("connectorEndpoint"),
                        c.TITLE.as("connectorTitle"),
                        c.DESCRIPTION.as("connectorDescription"),
                        c.ONLINE_STATUS.as("connectorOnlineStatus"),
                        d.ASSET_PROPERTIES.cast(String.class).as("assetPropertiesJson"),
                        d.CREATED_AT,
                        d.UPDATED_AT,
                        offlineSinceOrLastUpdatedAt.as("offlineSinceOrLastUpdatedAt"),
                        getContractOffers(d.CONNECTOR_ENDPOINT, d.ASSET_ID).as("contractOffers")
                )
                .from(c, d)
                .where(
                        c.ONLINE_STATUS.eq(ConnectorOnlineStatus.ONLINE),
                        filterBySearchQuery
                )
                .orderBy(getOrderBy(sorting, c, d, assetTitle))
                .fetchInto(DataOfferDbRow.class);
    }

    @NotNull
    private List<OrderField<?>> getOrderBy(CatalogPageSortingType sorting, Connector c, DataOffer d, Field<String> assetTitle) {
        List<OrderField<?>> orderBy;
        if (sorting == null || sorting == CatalogPageSortingType.TITLE) {
            orderBy = List.of(assetTitle.asc(), c.ENDPOINT.asc());
        } else if (sorting == CatalogPageSortingType.MOST_RECENT) {
            orderBy = List.of(d.CREATED_AT.desc(), c.TITLE.asc());
        } else if (sorting == CatalogPageSortingType.ORIGINATOR) {
            orderBy = List.of(c.ENDPOINT.asc(), assetTitle.asc());
        } else {
            throw new IllegalArgumentException("Unknown %s: %s".formatted(CatalogPageSortingType.class.getName(), sorting));
        }
        return orderBy;
    }

    private Field<List<DataOfferContractOfferDbRow>> getContractOffers(Field<String> connectorEndpoint, Field<String> assetId) {
        var dco = Tables.DATA_OFFER_CONTRACT_OFFER;

        var query = DSL.select(
                dco.CONTRACT_OFFER_ID,
                dco.POLICY.cast(String.class).as("policyJson"),
                dco.CREATED_AT,
                dco.UPDATED_AT
        ).from(dco).where(
                dco.CONNECTOR_ENDPOINT.eq(connectorEndpoint),
                dco.ASSET_ID.eq(assetId)).orderBy(dco.CREATED_AT.desc()
        );

        return DSL.multiset(query).convertFrom(it -> it.into(DataOfferContractOfferDbRow.class));
    }
}
