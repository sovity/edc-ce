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

package de.sovity.edc.ext.brokerserver.dao.pages.connector;

import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorDetailsRs;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.math.BigDecimal;

public class ConnectorDetailQueryService {
    public ConnectorDetailsRs queryConnectorDetailPage(DSLContext dsl, String connectorEndpoint) {
        var c = Tables.CONNECTOR;

        return dsl.select(
                c.ENDPOINT.as("endpoint"),
                c.PARTICIPANT_ID.as("participantId"),
                c.CREATED_AT.as("createdAt"),
                c.LAST_SUCCESSFUL_REFRESH_AT.as("lastSuccessfulRefreshAt"),
                c.LAST_REFRESH_ATTEMPT_AT.as("lastRefreshAttemptAt"),
                c.ONLINE_STATUS.as("onlineStatus"),
                dataOfferCount(c.ENDPOINT).as("numDataOffers"),
                getAvgSuccessfulCrawlTimeInMs(c).as("connectorCrawlingTimeAvg"))
            .from(c)
            .where(c.ENDPOINT.eq(connectorEndpoint))
            .groupBy(c.ENDPOINT)
            .fetchOneInto(ConnectorDetailsRs.class);
    }

    @NotNull
    private Field<BigDecimal> getAvgSuccessfulCrawlTimeInMs(Connector c) {
        var betm = Tables.BROKER_EXECUTION_TIME_MEASUREMENT;
        return DSL.select(DSL.avg(betm.DURATION_IN_MS))
            .from(betm)
            .where(betm.CONNECTOR_ENDPOINT.eq(c.ENDPOINT), betm.ERROR_STATUS.eq(MeasurementErrorStatus.OK))
            .asField();
    }

    private Field<Long> dataOfferCount(Field<String> endpoint) {
        var d = Tables.DATA_OFFER;
        return DSL.select(DSL.count()).from(d).where(d.CONNECTOR_ENDPOINT.eq(endpoint)).asField();
    }
}
