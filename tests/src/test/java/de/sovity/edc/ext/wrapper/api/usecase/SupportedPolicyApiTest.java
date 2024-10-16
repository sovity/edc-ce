/*
 * Copyright (c) 2024 sovity GmbH
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
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class SupportedPolicyApiTest {

    @RegisterExtension
    static CeIntegrationTestExtension providerExtension = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @Test
    void supportedPolicies(EdcClient client) {
        // act
        var actual = client.useCaseApi().getSupportedFunctions();

        // assert
        assertThat(actual).contains("ALWAYS_TRUE", "https://w3id.org/edc/v0.0.1/ns/inForceDate");
    }
}
