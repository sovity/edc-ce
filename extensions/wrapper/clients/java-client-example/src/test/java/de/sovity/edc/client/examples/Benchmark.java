package de.sovity.edc.client.examples;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.utils.jsonld.vocab.Prop;

import java.time.OffsetDateTime;
import java.util.List;

public class Benchmark {

    private static String PROVIDER_DSP = "https://load-test-1.stage-sovity.azure.sovity.io/control/api/v1/dsp";
    private static String PROVIDER_ID = "BPNL0000000A0UNJ";

    public static void main(String[] args) {
        var provider = EdcClient.builder()
            .managementApiUrl("https://load-test-1.stage-sovity.azure.sovity.io/control/data")
            .managementApiKey("LzyGxc8AMWrY+WKIrjd6yVzragE0vztZttzjWcopsBs=")
            .build();

        provider.testConnection();

        var consumer = EdcClient.builder()
            .managementApiUrl("https://load-test-1-consumer.stage-sovity.azure.sovity.io/control/data")
            .managementApiKey("lOtQEZ/prbEtoGMlNjFsB3SNV1HSO00e7iSIyTKcrEo=")
            .build();

        consumer.testConnection();

        create(provider, consumer, 0, 1);
    }

    private static void create(EdcClient provider, EdcClient consumer, int offset, int amount) {
        int index = offset;

        createAsset(provider, index);
        createPolicy(provider, index);
        createContractDefinition(provider, index);
        negotiateContract(consumer, index);
    }

    private static void negotiateContract(EdcClient client, int index) {
        client.uiApi().initiateContractNegotiation(
            ContractNegotiationRequest.builder()
                .counterPartyId(PROVIDER_ID)
                .counterPartyAddress(PROVIDER_DSP)
                .assetId("asset-" + index)
                .contractOfferId("contract-offer-" + index)
                .policyJsonLd("policy-" + index)
                .build()
        );
    }

    private static void createContractDefinition(EdcClient client, int index) {
        client.uiApi().createContractDefinition(
            ContractDefinitionRequest.builder()
                .contractDefinitionId("contract-definition-" + index)
                .accessPolicyId("policy-" + index)
                .contractPolicyId("policy-" + index)
                .assetSelector(List.of(UiCriterion.builder()
                    .operandLeft(Prop.Edc.ID)
                    .operator(UiCriterionOperator.EQ)
                    .operandRight(UiCriterionLiteral.builder()
                        .type(UiCriterionLiteralType.VALUE)
                        .value("asset-" + index)
                        .build())
                    .build()))
                .build()
        );
    }

    private static void createPolicy(EdcClient client, int index) {
        var afterYesterday = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value(OffsetDateTime.now().minusDays(1).toString())
                    .build())
                .build())
            .build();

        var beforeTomorrow = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.LT)
                .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value(OffsetDateTime.now().plusDays(1).toString())
                    .build())
                .build())
            .build();

        var expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(List.of(afterYesterday, beforeTomorrow))
            .build();

        client.uiApi().createPolicyDefinitionV2(
            PolicyDefinitionCreateDto.builder()
                .policyDefinitionId("policy-" + index)
                .expression(expression)
                .build()
        );
    }

    private static void createAsset(EdcClient client, int index) {
        client.uiApi().createAsset(
            UiAssetCreateRequest.builder()
                .title("Test Asset " + index)
                .id("asset-" + index)
                .dataSource(
                    UiDataSource.builder()
                        .type(DataSourceType.HTTP_DATA)
                        .httpData(UiDataSourceHttpData.builder()
                            .baseUrl("http://example.com")
                            .build())
                        .build()
                )
                .build()
        );
    }
}
