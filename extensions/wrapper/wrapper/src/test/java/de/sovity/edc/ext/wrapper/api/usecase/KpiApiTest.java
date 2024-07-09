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

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class KpiApiTest {
    private static final String PARTICIPANT_ID = "someid";

    private ConnectorConfig config;

    @RegisterExtension
    static final EdcExtension EDC_CONTEXT = new EdcExtension();

    @RegisterExtension
    static final TestDatabase DATABASE = TestDatabaseFactory.getTestDatabase(1);

    private ConnectorRemote connector;

    private EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        // set up provider EDC + Client
        // TODO: try to fix again after RT's PR. The EDC uses the DSP port 34003 instead of the dynamically allocated one...
        config = forTestDatabase(PARTICIPANT_ID, 34000, DATABASE);
        EDC_CONTEXT.setConfiguration(config.getProperties());
        connector = new ConnectorRemote(fromConnectorConfig(config));

        client = EdcClient.builder()
            .managementApiUrl(config.getManagementEndpoint().getUri().toString())
            .managementApiKey(config.getProperties().get("edc.api.auth.key"))
            .build();
    }

    @Test
    void getKpis() {
        // act
        var actual = client.useCaseApi().getKpis();

        // assert
        assertThat(actual.getAssetsCount()).isZero();
        assertThat(actual.getContractAgreementsCount()).isZero();
        assertThat(actual.getContractDefinitionsCount()).isZero();
        assertThat(actual.getPoliciesCount()).isEqualTo(1);
        assertThat(actual.getTransferProcessDto().getIncomingTransferProcessCounts()).isEmpty();
        assertThat(actual.getTransferProcessDto().getOutgoingTransferProcessCounts()).isEmpty();
        assertThat(actual.getAssetsCount()).isZero();
    }
}
