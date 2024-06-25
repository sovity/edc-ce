/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.contactcancellation.controller;

import de.sovity.edc.extension.db.directaccess.DatabaseDirectAccessExtension;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import jakarta.ws.rs.core.Response;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.mock;

class ContractCancellationControllerTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();

    // TODO: use transactions to save containers boot time
    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = new TestDatabaseViaTestcontainers();

    private ConnectorRemote providerConnector;

    private ConnectorConfig providerConfig;

    @BeforeEach
    void setup() {
        providerEdcContext.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));

        providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 21000, PROVIDER_DATABASE);
        providerConfig.setProperty(DatabaseDirectAccessExtension.EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS, "1000");
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));
    }

    @Test
    void cancel_whenNoCancellationExists_shouldReturn200AndCancelContract() {

        // TODO PSQLException: ERROR: relation "edc_transfer_process" does not exist
        //  Is this related to EDC extensions loading order?
        //  How to change that?

        // act
        given()
            // TODO: add validators
            .body("""
                {
                    "contractId" : "contract-1",
                    "reason" : "some reason 1",
                    "detail" : "some detail 1",
                }""")
            // TODO: is sovity the best place? Any precedent?
            .post(providerConfig.getManagementEndpoint().getUri() + "/sovity/contract/cancel")
            .then()

            // assert
            .statusCode(Response.Status.OK.getStatusCode());

    }

    // TODO: don't double cancel a contract
}
