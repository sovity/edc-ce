package de.sovity.edc.e2e.common;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import jakarta.ws.rs.HttpMethod;
import lombok.val;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

public class AlwaysTruePolicyMigrationCommonTest {

    public static void alwaysTruePolicyMigrationTest(E2eScenario scenario, ClientAndServer mockServer, EdcClient providerClient, EdcClient consumerClient) {
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
