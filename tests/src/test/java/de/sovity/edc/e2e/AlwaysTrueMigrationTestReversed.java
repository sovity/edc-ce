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
import static org.assertj.core.api.Assertions.assertThat;

class AlwaysTrueMigrationTestReversed {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = E2eTestExtension.builder()
        .additionalConsumerMigrationLocation("classpath:db/additional-test-data/always-true-policy-legacy")
        .additionalProviderMigrationLocation("classpath:db/additional-test-data/always-true-policy-migrated")
        .build();

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
