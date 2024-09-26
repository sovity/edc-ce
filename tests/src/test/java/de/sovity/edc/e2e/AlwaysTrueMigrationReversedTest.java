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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.config.ConfigProps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;

import static de.sovity.edc.e2e.AlwaysTrueMigrationTest.testTransfer;
import static de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory.defaultBuilder;

class AlwaysTrueMigrationReversedTest {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = new E2eTestExtension(
        defaultBuilder().toBuilder()
            .consumerConfigCustomizer(config -> config.setProperty(
                ConfigProps.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS, "classpath:db/additional-test-data/always-true-policy-legacy"
            ))
            .providerConfigCustomizer(config -> config.setProperty(
                ConfigProps.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS, "classpath:db/additional-test-data/always-true-policy-migrated"
            ))
            .build()
    );

    @Test
    @DisabledOnGithub
    void test_migrated_policy_working_test_legacy_policy_working(
        E2eScenario scenario,
        ClientAndServer mockServer,
        @Provider EdcClient providerClient,
        @Consumer EdcClient consumerClient
    ) {
        // assert correct policies
        testTransfer(scenario, mockServer, consumerClient);
    }
}
