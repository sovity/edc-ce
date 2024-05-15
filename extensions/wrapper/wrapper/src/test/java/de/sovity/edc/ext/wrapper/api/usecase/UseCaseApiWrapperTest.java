package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.CatalogFilterExpression;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteral;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteralType;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionOperator;
import de.sovity.edc.client.gen.model.CatalogQueryParams;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
public class UseCaseApiWrapperTest {
    private EdcClient client;
    private final String dataOfferId = "my-data-offer-2023-11";

    private String data = "expected data from data source";

    private String assetId1;
    private String assetId2;
    private String policyId;


    @BeforeEach
    void setup(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();

        assetId1 = client.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("test-asset-1")
                .title("Test Asset 1")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, TestUtils.PROTOCOL_ENDPOINT
                ))
                .build()).getId();

        assetId2 = client.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("test-asset-2")
                .title("Test Asset 2")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, TestUtils.PROTOCOL_ENDPOINT
                ))
                .build()).getId();

        policyId = client.uiApi().createPolicyDefinition(PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build()).getId();
    }

    @Test
    void shouldFetchFilteredDataOffersWithEq() {
        // arrange
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = buildCatalogQueryParams(assetId1, CatalogFilterExpressionOperator.EQ);
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).isEqualTo(assetId1);
        assertThat(dataOffers.get(0).getAsset().getTitle()).isEqualTo("Test Asset 1");

    }

    @Test
    void shouldFetchFilteredDataOffersWithLike() {
        // arrange
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = buildCatalogQueryParams(assetId2, CatalogFilterExpressionOperator.LIKE);
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).isEqualTo(assetId2);
        assertThat(dataOffers.get(0).getAsset().getTitle()).isEqualTo("Test Asset 2");

    }

    @Test
    void shouldFetchFilteredDataOffersWithIn() {
        // arrange
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = buildCatalogQueryParamsWithIn(List.of(assetId1, assetId2));
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(2);
        assertThat(dataOffers.stream().map(d -> d.getAsset().getAssetId())).containsExactlyInAnyOrder(assetId1, assetId2);
    }

    @Test
    void shouldFetchWithoutFilterButWithLimit() {
        // arrange
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = buildCatalogQueryParamsWithLimit(1, 0);
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).matches(assetId1 + "|" + assetId2);
    }

    private CatalogQueryParams buildCatalogQueryParams(String assetId, CatalogFilterExpressionOperator operator) {
        return CatalogQueryParams.builder()
                .targetEdc(TestUtils.PROTOCOL_ENDPOINT)
                .filterExpression(CatalogFilterExpression.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(operator)
                        .operandRight(CatalogFilterExpressionLiteral.builder().value(assetId).type(CatalogFilterExpressionLiteralType.VALUE).build())
                        .build()
                )
                .build();
    }

    private CatalogQueryParams buildCatalogQueryParamsWithIn(List<String> assetIds) {
        return CatalogQueryParams.builder()
                .targetEdc(TestUtils.PROTOCOL_ENDPOINT)
                .filterExpression(CatalogFilterExpression.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(CatalogFilterExpressionOperator.IN)
                        .operandRight(CatalogFilterExpressionLiteral.builder().valueList(assetIds).type(CatalogFilterExpressionLiteralType.VALUE_LIST).build())
                        .build()
                )
                .build();
    }

    private CatalogQueryParams buildCatalogQueryParamsWithLimit(Integer limit, Integer offset) {
        return CatalogQueryParams.builder()
                .targetEdc(TestUtils.PROTOCOL_ENDPOINT)
                .limit(limit)
                .offset(offset)
                .build();
    }

    private void buildContractDefinition(String policyId, String assetId1, String cdId) {
        client.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
                .contractDefinitionId(cdId)
                .accessPolicyId(policyId)
                .contractPolicyId(policyId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(assetId1)
                                .build())
                        .build()))
                .build());
    }
}
