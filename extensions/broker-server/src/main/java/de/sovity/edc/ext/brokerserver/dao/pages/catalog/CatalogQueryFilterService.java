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

import de.sovity.edc.ext.brokerserver.dao.AssetProperty;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.dao.utils.SearchUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CatalogQueryFilterService {
    private final BrokerServerSettings brokerServerSettings;

    public Condition filter(CatalogQueryFields fields, String searchQuery, List<CatalogQueryFilter> filters) {
        var conditions = new ArrayList<Condition>();
        conditions.add(SearchUtils.simpleSearch(searchQuery, List.of(
                fields.getAssetId(),
                fields.getAssetName(),
                fields.getAssetProperty(AssetProperty.DATA_CATEGORY),
                fields.getAssetProperty(AssetProperty.DATA_SUBCATEGORY),
                fields.getAssetDescription(),
                fields.getAssetKeywords(),
                fields.getConnectorTable().ENDPOINT
        )));
        conditions.add(onlyOnlineOrRecentlyOfflineConnectors(fields.getConnectorTable()));
        conditions.addAll(filters.stream().map(CatalogQueryFilter::queryFilterClauseOrNull)
                .filter(Objects::nonNull).map(it -> it.filterDataOffers(fields)).toList());
        return DSL.and(conditions);
    }

    @NotNull
    private Condition onlyOnlineOrRecentlyOfflineConnectors(Connector c) {
        var maxOfflineDuration = brokerServerSettings.getHideOfflineDataOffersAfter();

        Condition maxOfflineDurationNotExceeded;
        if (maxOfflineDuration == null) {
            maxOfflineDurationNotExceeded = DSL.trueCondition();
        } else {
            maxOfflineDurationNotExceeded = c.LAST_SUCCESSFUL_REFRESH_AT.greaterThan(OffsetDateTime.now().minus(maxOfflineDuration));
        }

        return DSL.or(
                c.ONLINE_STATUS.eq(ConnectorOnlineStatus.ONLINE),
                maxOfflineDurationNotExceeded
        );
    }
}
