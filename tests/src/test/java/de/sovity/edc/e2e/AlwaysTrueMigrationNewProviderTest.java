package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.e2e.common.AlwaysTruePolicyMigrationCommonTest;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AlwaysTrueMigrationNewProviderTest {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = new E2eTestExtension(
        "classpath:db/additional-test-data/alwaysTruePolicyMigrationTest",
        ""
    );

    @Test
    public void always_true_agreements_still_work_after_migration(
        E2eScenario scenario,
        ClientAndServer mockServer,
        @Provider EdcClient providerClient,
        @Consumer EdcClient consumerClient
    ) {
        // assert correct policies
        val newAlwaysTruePolicyOnProvider = providerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow();

        val oldAlwaysTruePolicyOnConsumer = consumerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow();

        UiPolicyExpression oldAlwaysTrueExpression = oldAlwaysTruePolicyOnConsumer.getPolicy().getExpression();
        UiPolicyExpression newAlwaysTrueExpression = newAlwaysTruePolicyOnProvider.getPolicy().getExpression();

        assertThat(oldAlwaysTrueExpression.getType()).isEqualTo(UiPolicyExpressionType.CONSTRAINT);
        assertThat(oldAlwaysTrueExpression.getConstraint().getLeft()).isEqualTo(AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE);
        assertThat(oldAlwaysTrueExpression.getConstraint().getRight().getValue()).isEqualTo(AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE);
        assertThat(oldAlwaysTrueExpression.getConstraint().getOperator()).isEqualTo(OperatorDto.EQ);

        assertThat(newAlwaysTrueExpression.getType()).isEqualTo(UiPolicyExpressionType.EMPTY);
        assertThat(newAlwaysTrueExpression.getConstraint()).isNull();

        AlwaysTruePolicyMigrationCommonTest.alwaysTruePolicyMigrationTest(scenario, mockServer, providerClient, consumerClient);
    }


}
