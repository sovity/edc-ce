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
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.RuntimePerClassWithDbExtension;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class SupportedPolicyApiTest {

    private static ConnectorConfig config;
    private static EdcClient client;

    @RegisterExtension
    static RuntimePerClassWithDbExtension providerExtension = new RuntimePerClassWithDbExtension(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            config = CeIntegrationTestUtils.defaultConfig("my-edc-participant-id", testDatabase);
            client = EdcClient.builder()
                .managementApiUrl(config.getManagementApiUrl())
                .managementApiKey(config.getManagementApiKey())
                .build();
            return config.getProperties();
        }
    );

    @Test
    void supportedPolicies() {
        // act
        var actual = client.useCaseApi().getSupportedFunctions();

        // assert
        assertThat(actual).contains("ALWAYS_TRUE", "https://w3id.org/edc/v0.0.1/ns/inForceDate");
    }
}
