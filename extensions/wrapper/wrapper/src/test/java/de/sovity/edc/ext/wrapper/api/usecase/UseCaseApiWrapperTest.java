/*
 *  Copyright (c) 2024 sovity GmbH
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
import de.sovity.edc.client.gen.model.CatalogFilterExpression;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteral;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteralType;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionOperator;
import de.sovity.edc.client.gen.model.CatalogQuery;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class UseCaseApiWrapperTest {
    private EdcClient client;

    private String assetId1 = "test-asset-1";
    private String assetId2 = "test-asset-2";
    private String policyId = "policy-1";

    private static final String PARTICIPANT_ID = "someid";

    private ConnectorConfig config;

    @RegisterExtension
    static final EdcExtension EDC_CONTEXT = new EdcExtension();

    @RegisterExtension
    static final TestDatabase DATABASE = TestDatabaseFactory.getTestDatabase(1);

    private ConnectorRemote connector;

    private EdcClient client;

    @BeforeEach
    void setup(EdcExtension extension) {
        // set up provider EDC + Client
        // TODO: try to fix again after RT's PR. The EDC uses the DSP port 34003 instead of the dynamically allocated one...
        config = forTestDatabase(PARTICIPANT_ID, 34000, DATABASE);
        EDC_CONTEXT.setConfiguration(config.getProperties());
        connector = new ConnectorRemote(fromConnectorConfig(config));

        client = EdcClient.builder()
            .managementApiUrl(config.getManagementEndpoint().getUri().toString())
            .managementApiKey(config.getProperties().get("edc.api.auth.key"))
            .build();
    }

    @Test
    @DisabledOnGithub
    void shouldFetchFilteredDataOffersWithEq() {
        // arrange
        setupAssets();
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(Prop.Edc.ID, CatalogFilterExpressionOperator.EQ, "test-asset-1");
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).isEqualTo(assetId1);
        assertThat(dataOffers.get(0).getAsset().getTitle()).isEqualTo("Test Asset 1");

    }

    @Test
    @DisabledOnGithub
    void shouldFetchFilteredDataOffersWithIn() {
        // arrange
        setupAssets();
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(Prop.Edc.ID, CatalogFilterExpressionOperator.IN, List.of("test-asset-1", "test-asset-2"));
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(2);
        assertThat(dataOffers)
                .extracting(it -> it.getAsset().getAssetId())
                .containsExactlyInAnyOrder(assetId1, assetId2);
    }

    @Test
    @DisabledOnGithub
    void shouldFetchWithoutFilterButWithLimit() {
        // arrange
        setupAssets();
        buildContractDefinition(policyId, assetId1, "cd-1");
        buildContractDefinition(policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(1, 0);
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers)
                .extracting(it -> it.getAsset().getAssetId())
                .containsAnyOf(assetId1, assetId2);
    }

    private CatalogQuery criterion(String leftOperand, CatalogFilterExpressionOperator operator, String rightOperand) {
        return CatalogQuery.builder()
                .connectorEndpoint(TestUtils.PROTOCOL_ENDPOINT)
                .filterExpressions(
                        List.of(
                            CatalogFilterExpression.builder()
                            .operandLeft(leftOperand)
                            .operator(operator)
                            .operandRight(CatalogFilterExpressionLiteral.builder().value(rightOperand).type(CatalogFilterExpressionLiteralType.VALUE).build())
                            .build()
                        )
                )
                .build();
    }

    private CatalogQuery criterion(String leftOperand, CatalogFilterExpressionOperator operator, List<String> rightOperand) {
        return CatalogQuery.builder()
                .connectorEndpoint(TestUtils.PROTOCOL_ENDPOINT)
                .filterExpressions(
                        List.of(
                            CatalogFilterExpression.builder()
                            .operandLeft(leftOperand)
                            .operator(operator)
                            .operandRight(CatalogFilterExpressionLiteral.builder().valueList(rightOperand).type(CatalogFilterExpressionLiteralType.VALUE_LIST).build())
                            .build()
                        )
                )
                .build();
    }

    private CatalogQuery criterion(Integer limit, Integer offset) {
        return CatalogQuery.builder()
                .connectorEndpoint(TestUtils.PROTOCOL_ENDPOINT)
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

    private void setupAssets() {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(TestUtils.PROTOCOL_ENDPOINT)
                .build())
            .build();

        assetId1 = client.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("test-asset-1")
                .title("Test Asset 1")
                .dataSource(dataSource)
                .mediaType("application/json")
                .build()).getId();

        assetId2 = client.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("test-asset-2")
                .title("Test Asset 2")
                .dataSource(dataSource)
                .mediaType("application/json")
                .build()).getId();

        policyId = client.uiApi().createPolicyDefinition(PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build()).getId();
    }
}
