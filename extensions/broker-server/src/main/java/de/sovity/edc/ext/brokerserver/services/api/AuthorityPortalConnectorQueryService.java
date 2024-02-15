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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryFields;
import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.List;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;

@RequiredArgsConstructor
public class AuthorityPortalConnectorQueryService {

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ConnectorMetadataRs {
        String connectorEndpoint;
        String participantId;
        ConnectorOnlineStatus onlineStatus;
        OffsetDateTime offlineSinceOrLastUpdatedAt;
        Integer dataOfferCount;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ConnectorDetailsRs {
        String connectorEndpoint;
        String participantId;
        ConnectorOnlineStatus onlineStatus;
        OffsetDateTime offlineSinceOrLastUpdatedAt;
        List<DataOfferRs> dataOffers;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DataOfferRs {
        String dataOfferId;
        String dataOfferName;
    }

    @NotNull
    public List<ConnectorMetadataRs> getConnectorMetadata(DSLContext dsl, List<String> endpoints) {
        var c = Tables.CONNECTOR;

        return dsl.select(
                c.ENDPOINT.as("connectorEndpoint"),
                c.PARTICIPANT_ID.as("participantId"),
                c.ONLINE_STATUS.as("onlineStatus"),
                CatalogQueryFields.offlineSinceOrLastUpdatedAt(c).as("offlineSinceOrLastUpdatedAt"),
                getDataOfferCount(c.ENDPOINT).as("dataOfferCount")
            )
            .from(c)
            .where(PostgresqlUtils.in(c.ENDPOINT, endpoints))
            .fetchInto(ConnectorMetadataRs.class);
    }

    @NotNull
    public Field<Integer> getDataOfferCount(Field<String> connectorEndpoint) {
        var d = Tables.DATA_OFFER;

        return select(coalesce(count().cast(Integer.class), DSL.value(0)))
            .from(d)
            .where(d.CONNECTOR_ENDPOINT.eq(connectorEndpoint))
            .asField();
    }

    @NotNull
    public List<DataOfferRs> getDataOffers(DSLContext dsl, String connectorEndpoint) {
        var d = Tables.DATA_OFFER;

        return dsl.select(
                d.ASSET_TITLE.as("dataOfferName"),
                d.ASSET_ID.as("dataOfferId")
            )
            .from(d)
            .where(d.CONNECTOR_ENDPOINT.eq(connectorEndpoint))
            .fetchInto(DataOfferRs.class);
    }


    @NotNull
    public List<ConnectorDetailsRs> getConnectorsDataOffers(DSLContext dsl, List<String> endpoints) {
        var c = Tables.CONNECTOR;

        var connectors = dsl.select(
                c.ENDPOINT.as("connectorEndpoint"),
                c.PARTICIPANT_ID.as("participantId"),
                c.ONLINE_STATUS.as("onlineStatus"),
                CatalogQueryFields.offlineSinceOrLastUpdatedAt(c).as("offlineSinceOrLastUpdatedAt")
            )
            .from(c)
            .where(PostgresqlUtils.in(c.ENDPOINT, endpoints))
            .fetchInto(ConnectorDetailsRs.class);
        connectors.forEach(connector -> connector.dataOffers = getDataOffers(dsl, connector.connectorEndpoint));
        return connectors;
    }
}
