package de.sovity.edc.ext.wrapper;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
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
import lombok.val;
import org.awaitility.Awaitility;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

public class Benchmark {

    private static final String PROVIDER_DSP = "https://load-test-3-provider.stage-sovity.azure.sovity.io/control/api/v1/dsp";
    private static final String PROVIDER_ID = "BPNL00000000024R";

    public static void main(String[] args) {
        var provider = EdcClient.builder()
            .managementApiUrl("https://load-test-3-provider.stage-sovity.azure.sovity.io/control/data")
            .managementApiKey("GcR0t0BFx0kSHh/8B43rUDyPCFRMoOko+CiuhuMLMrs=")
            .build();

        provider.testConnection();

        var consumer = EdcClient.builder()
            .managementApiUrl("https://load-test-3-consumer.stage-sovity.azure.sovity.io/control/data")
            .managementApiKey("77mBZTNriBPAb0WwIJn95n++O/GkVL8fNovLFPa/GlE=")
            .build();

        provider.testConnection();

        val start = 5000;
        val stop = 5050;

        for (int i = start; i < stop; ++i) {
            create(provider, consumer, i);
        }
    }

    private static void create(
        EdcClient provider,
        EdcClient consumer,
        int index
    ) {
        System.out.println("Index=" + index);
        createAsset(provider, index);
        createPolicy(provider, index);
        createContractDefinition(provider, index);

        if (false) {
            val negotiation = negotiate(provider, consumer, index);

            consumer.uiApi().initiateTransfer(
                InitiateTransferRequest.builder()
                    .contractAgreementId(negotiation.getContractAgreementId())
                    .transferType("HttpData-PUSH")
                    .dataSinkProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.BASE_URL, "https://example.com",
                        Prop.Edc.METHOD, "POST"
                    ))
                    .build()
            );
        }
    }

    private static UiContractNegotiation negotiate(EdcClient provider, EdcClient consumer, int index) {
        System.out.println("Negotiating " + index + "...");
        val offers = provider.uiApi().getCatalogPageDataOffers(PROVIDER_ID, PROVIDER_DSP);

        val firstContractOffer = offers.stream()
            .filter(offer -> offer.getAsset().getAssetId().equals("asset-" + index))
            .findFirst()
            .get()
            .getContractOffers()
            .get(0);

        val negotiation = consumer.uiApi().initiateContractNegotiation(
            ContractNegotiationRequest.builder()
                .counterPartyId(PROVIDER_ID)
                .counterPartyAddress(PROVIDER_DSP)
                .assetId("asset-" + index)
                .contractOfferId(firstContractOffer.getContractOfferId())
                .policyJsonLd(firstContractOffer.getPolicy().getPolicyJsonLd())
                .build()
        );

        Awaitility.await().atMost(ofSeconds(100)).pollDelay(ofMillis(1000)).until(
            () -> {
                System.out.println("Awaiting " + index + "...");
                return consumer
                    .uiApi()
                    .getContractNegotiation(negotiation.getContractNegotiationId())
                    .getState()
                    .getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS;
            }
        );

        System.out.println("Negotiated " + index);
        return negotiation;
    }

    private static void createContractDefinition(EdcClient client, int index) {
        System.out.println("Create contract definition " + index);
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
        System.out.println("Create policies " + index);
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
        System.out.println("Create asset " + index);
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
