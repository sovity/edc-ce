package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;

import static de.sovity.edc.e2e.AlwaysTrueMigrationTest.testTransfer;
import static de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory.defaultBuilder;

class AlwaysTrueMigrationReversedTest {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = new E2eTestExtension(
        defaultBuilder()
            .consumerConfigCustomizer(config -> config.getProperties()
                .put("edc.flyway.additional.migration.locations", "classpath:db/additional-test-data/always-true-policy-legacy"))
            .providerConfigCustomizer(config -> config.getProperties()
                .put("edc.flyway.additional.migration.locations", "classpath:db/additional-test-data/always-true-policy-migrated"))
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
