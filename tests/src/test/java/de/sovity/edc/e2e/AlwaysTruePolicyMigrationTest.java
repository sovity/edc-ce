package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import jakarta.ws.rs.HttpMethod;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

public class AlwaysTruePolicyMigrationTest {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = new E2eTestExtension();

    @Test
    public void always_true_agreements_still_work_after_migration(
        E2eScenario scenario,
        ClientAndServer mockServer,
        @Provider EdcClient providerClient,
        @Consumer EdcClient consumerClient
    ) {
        // assert correct policies
        val oldAlwaysTruePolicyOnProvider = providerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow();

        val newAlwaysTruePolicyOnConsumer = consumerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow();

        assertThat(oldAlwaysTruePolicyOnProvider.getPolicy().getExpression().getType()).isEqualTo(UiPolicyExpressionType.CONSTRAINT);
        assertThat(oldAlwaysTruePolicyOnProvider.getPolicy().getExpression().getConstraint().getLeft()).isEqualTo(AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE);
        assertThat(oldAlwaysTruePolicyOnProvider.getPolicy().getExpression().getConstraint().getRight()).isEqualTo(AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE);
        assertThat(oldAlwaysTruePolicyOnProvider.getPolicy().getExpression().getConstraint().getOperator()).isEqualTo(OperatorDto.EQ);

        assertThat(newAlwaysTruePolicyOnConsumer.getPolicy().getExpression().getType()).isEqualTo(UiPolicyExpressionType.EMPTY);
        assertThat(oldAlwaysTruePolicyOnProvider.getPolicy().getExpression().getConstraint()).isNull();

        // arrange
        val destinationPath = "/destination/some/path/";
        val destinationUrl = "http://localhost:" + mockServer.getPort() + destinationPath;
        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        val asset = scenario.createAsset();
        scenario.createContractDefinition(asset); //this automatically uses the always-true policy
        val negotiation = scenario.negotiateAssetAndAwait(asset);

        // act
        val transfer = scenario.transferAndAwait(
            InitiateTransferRequest.builder()
                .contractAgreementId(negotiation.getContractAgreementId())
                .dataSinkProperties(
                    Map.of(
                        EDC_NAMESPACE + "baseUrl", destinationUrl,
                        EDC_NAMESPACE + "method", HttpMethod.POST,
                        EDC_NAMESPACE + "type", "HttpData"
                    )
                )
                .build()
        );
        val transferProcess = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().stream().filter(
            process -> process.getTransferProcessId().equals(transfer)
        ).findFirst().orElseThrow();

        // assert
        assertThat(transferProcess.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }
}
