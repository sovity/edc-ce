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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.utils;

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import lombok.experimental.UtilityClass;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

@UtilityClass
public class TestData {

    public static void insertConnector(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Consumer<ConnectorRecord> applier
    ) {
        var organization = dsl.newRecord(Tables.ORGANIZATION);
        organization.setId(connectorRef.getOrganizationId());
        organization.setName(connectorRef.getOrganizationLegalName());
        organization.insert();

        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setEnvironment(connectorRef.getEnvironmentId());
        connector.setOrganizationId(connectorRef.getOrganizationId());
        connector.setConnectorId(connectorRef.getConnectorId());
        connector.setName(connectorRef.getConnectorId() + " Name");
        connector.setEndpointUrl(connectorRef.getEndpoint());
        connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        connector.setLastRefreshAttemptAt(null);
        connector.setLastSuccessfulRefreshAt(null);
        connector.setCreatedAt(OffsetDateTime.now());
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        applier.accept(connector);
        connector.insert();
    }
}
