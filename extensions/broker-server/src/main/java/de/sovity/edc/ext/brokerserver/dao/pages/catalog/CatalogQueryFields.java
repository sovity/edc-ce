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

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.DataOffer;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.DataOfferViewCount;
import de.sovity.edc.ext.brokerserver.services.config.DataSpaceConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;

import static org.jooq.impl.DSL.coalesce;

/**
 * Tables and fields used in the catalog page query.
 * <p>
 * Having this as a class makes access to computed fields (e.g. asset properties) easier.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CatalogQueryFields {
    Connector connectorTable;
    DataOffer dataOfferTable;
    DataOfferViewCount dataOfferViewCountTable;

    // Asset Properties from JSON to be used in sorting / filtering
    Field<String> dataSpace;

    // This date should always be non-null
    // It's used in the UI to display the last relevant change date of a connector
    Field<OffsetDateTime> offlineSinceOrLastUpdatedAt;

    DataSpaceConfig dataSpaceConfig;

    public CatalogQueryFields(
            Connector connectorTable,
            DataOffer dataOfferTable,
            DataOfferViewCount dataOfferViewCountTable,
            DataSpaceConfig dataSpaceConfig
    ) {
        this.connectorTable = connectorTable;
        this.dataOfferTable = dataOfferTable;
        this.dataOfferViewCountTable = dataOfferViewCountTable;
        this.dataSpaceConfig = dataSpaceConfig;
        offlineSinceOrLastUpdatedAt = offlineSinceOrLastUpdatedAt(connectorTable);

        dataSpace = buildDataSpaceField(connectorTable, dataSpaceConfig);
    }

    private Field<String> buildDataSpaceField(Connector connectorTable, DataSpaceConfig dataSpaceConfig) {
        var endpoint = connectorTable.ENDPOINT;

        var connectors = dataSpaceConfig.dataSpaceConnectors();
        if (connectors.isEmpty()) {
            return DSL.val(dataSpaceConfig.defaultDataSpace());
        }

        var first = connectors.get(0);
        var dspCase = DSL.case_(endpoint).when(first.endpoint(), first.dataSpaceName());

        for (var dsp : connectors.subList(1, connectors.size())) {
            dspCase = dspCase.when(dsp.endpoint(), dsp.dataSpaceName());
        }

        return dspCase.else_(DSL.val(dataSpaceConfig.defaultDataSpace()));
    }

    public CatalogQueryFields withSuffix(String additionalSuffix) {
        return new CatalogQueryFields(
                connectorTable.as(withSuffix(connectorTable, additionalSuffix)),
                dataOfferTable.as(withSuffix(dataOfferTable, additionalSuffix)),
                dataOfferViewCountTable.as(withSuffix(dataOfferViewCountTable, additionalSuffix)),
                dataSpaceConfig
        );
    }

    private String withSuffix(Table<?> table, String additionalSuffix) {
        return "%s_%s".formatted(table.getName(), additionalSuffix);
    }

    public Field<Integer> getViewCount() {
        var subquery = DSL.select(DSL.count())
                .from(dataOfferViewCountTable)
                .where(dataOfferViewCountTable.ASSET_ID.eq(dataOfferTable.ASSET_ID)
                        .and(dataOfferViewCountTable.CONNECTOR_ENDPOINT.eq(connectorTable.ENDPOINT)));

        return subquery.asField();
    }

    public Field<String> getOrganizationName() {
        return organizationName(connectorTable.MDS_ID);
    }

    public static Field<OffsetDateTime> offlineSinceOrLastUpdatedAt(Connector connectorTable) {
        return DSL.coalesce(
                connectorTable.LAST_SUCCESSFUL_REFRESH_AT,
                connectorTable.CREATED_AT
        );
    }

    public static Field<String> organizationName(Field<String> mdsId) {
        var om = Tables.ORGANIZATION_METADATA;
        var organizationName = DSL.select(om.NAME)
                .from(om)
                .where(om.MDS_ID.eq(mdsId))
                .asField()
                .cast(String.class);
        return coalesce(organizationName, "Unknown");
    }
}
