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

package de.sovity.edc.client;

import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.sovity.edc.client.TestUtils.PROTOCOL_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class DashboardPageTest {
    EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void testDashboardPage() {
        // arrange

        // act
        var dashboardPage = client.uiApi().dashboardPageEndpoint();

        // assert
        assertThat(dashboardPage.getConnectorParticipantId()).isEqualTo("my-edc-participant-id");
        assertThat(dashboardPage.getConnectorDescription()).isEqualTo("My Connector Description");
        assertThat(dashboardPage.getConnectorTitle()).isEqualTo("My Connector");
        assertThat(dashboardPage.getConnectorEndpoint()).isEqualTo(PROTOCOL_ENDPOINT);
        assertThat(dashboardPage.getConnectorCuratorName()).isEqualTo("My Org");
        assertThat(dashboardPage.getConnectorCuratorUrl()).isEqualTo("https://connector.my-org");
        assertThat(dashboardPage.getConnectorMaintainerName()).isEqualTo("Maintainer Org");
        assertThat(dashboardPage.getConnectorMaintainerUrl()).isEqualTo("https://maintainer-org");
        assertThat(dashboardPage.getConnectorDapsConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorDapsConfig().getTokenUrl()).isEqualTo("https://token-url.daps");
        assertThat(dashboardPage.getConnectorDapsConfig().getJwksUrl()).isEqualTo("https://jwks-url.daps");
        assertThat(dashboardPage.getConnectorMiwConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorMiwConfig().getAuthorityId()).isEqualTo("my-authority-id");
        assertThat(dashboardPage.getConnectorMiwConfig().getUrl()).isEqualTo("https://miw");
        assertThat(dashboardPage.getConnectorMiwConfig().getTokenUrl()).isEqualTo("https://token.miw");
    }
}
