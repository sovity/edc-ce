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
import de.sovity.edc.client.gen.model.CatalogFilterExpression;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteral;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteralType;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionOperator;
import de.sovity.edc.client.gen.model.CatalogQuery;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.extension.e2e.junit.Janitor;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.config.ConfigUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UseCaseApiWrapper2Test {
    String protocolApiUrl;

    @RegisterExtension
    static CeIntegrationTestExtension providerExtension = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @BeforeEach
    public void setup(Config config) {
        protocolApiUrl = ConfigUtils.getProtocolApiUrl(config);
    }

    private static String assetId1 = "test-asset-1";
    private static String assetId2 = "test-asset-2";
    private static String policyId = "policy-1";

    @Test
    @DisabledOnGithub
    void shouldFetchFilteredDataOffersWithEq(
        EdcClient client,
        Janitor janitor
    ) {
        // arrange
        setupAssets(client, janitor);
        buildContractDefinition(client, policyId, assetId1, "cd-1");
        buildContractDefinition(client, policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(Prop.Edc.ID, CatalogFilterExpressionOperator.EQ, "test-asset-1");
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).isEqualTo(assetId1);
        assertThat(dataOffers.get(0).getAsset().getTitle()).isEqualTo("Test Asset 1");

        cleanup(client);
    }

    @Test
    @DisabledOnGithub
    void shouldFetchFilteredDataOffersWithIn(
        EdcClient client,
        Janitor janitor
    ) {
        // arrange
        setupAssets(client, janitor);
        buildContractDefinition(client, policyId, assetId1, "cd-1");
        buildContractDefinition(client, policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(Prop.Edc.ID, CatalogFilterExpressionOperator.IN, List.of("test-asset-1", "test-asset-2"));
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(2);
        assertThat(dataOffers)
            .extracting(it -> it.getAsset().getAssetId())
            .containsExactlyInAnyOrder(assetId1, assetId2);

        cleanup(client);
    }

    private static void cleanup(EdcClient client) {
        client.uiApi().deleteAsset(assetId1);
        client.uiApi().deleteAsset(assetId2);
        client.uiApi().deleteContractDefinition("cd-1");
        client.uiApi().deleteContractDefinition("cd-2");
        client.uiApi().deletePolicyDefinition(policyId);
    }

    @Test
    @DisabledOnGithub
    void shouldFetchWithoutFilterButWithLimit(
        EdcClient client,
        Janitor janitor
    ) {
        // arrange
        setupAssets(client, janitor);
        buildContractDefinition(client, policyId, assetId1, "cd-1");
        buildContractDefinition(client, policyId, assetId2, "cd-2");

        // act
        var catalogQueryParamsEq = criterion(1, 0);
        var dataOffers = client.useCaseApi().queryCatalog(catalogQueryParamsEq);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers)
            .extracting(it -> it.getAsset().getAssetId())
            .containsAnyOf(assetId1, assetId2);

        cleanup(client);
    }

    private CatalogQuery criterion(
        String leftOperand,
        CatalogFilterExpressionOperator operator,
        String rightOperand
    ) {
        return CatalogQuery.builder()
            .connectorEndpoint(protocolApiUrl)
            .filterExpressions(
                List.of(
                    CatalogFilterExpression.builder()
                        .operandLeft(leftOperand)
                        .operator(operator)
                        .operandRight(
                            CatalogFilterExpressionLiteral.builder().value(rightOperand).type(CatalogFilterExpressionLiteralType.VALUE)
                                .build())
                        .build()
                )
            )
            .build();
    }

    private CatalogQuery criterion(String leftOperand, CatalogFilterExpressionOperator operator, List<String> rightOperand) {
        return CatalogQuery.builder()
            .connectorEndpoint(protocolApiUrl)
            .filterExpressions(
                List.of(
                    CatalogFilterExpression.builder()
                        .operandLeft(leftOperand)
                        .operator(operator)
                        .operandRight(CatalogFilterExpressionLiteral.builder().valueList(rightOperand)
                            .type(CatalogFilterExpressionLiteralType.VALUE_LIST).build())
                        .build()
                )
            )
            .build();
    }

    private CatalogQuery criterion(Integer limit, Integer offset) {
        return CatalogQuery.builder()
            .connectorEndpoint(protocolApiUrl)
            .limit(limit)
            .offset(offset)
            .build();
    }

    private void buildContractDefinition(EdcClient client, String policyId, String assetId1, String cdId) {
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

    private void setupAssets(EdcClient client, Janitor janitor) {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(protocolApiUrl)
                .build())
            .build();

        int offset = 0;
        int firstIndex = offset + 1;
        assetId1 = janitor.withClient(client).createAsset(UiAssetCreateRequest.builder()
            .id("test-asset-" + firstIndex)
            .title("Test Asset " + firstIndex)
            .dataSource(dataSource)
            .mediaType("application/json")
            .build()).getId();

        int secondIndex = offset + 2;
        assetId2 = janitor.withClient(client).createAsset(UiAssetCreateRequest.builder()
            .id("test-asset-" + secondIndex)
            .title("Test Asset " + secondIndex)
            .dataSource(dataSource)
            .mediaType("application/json")
            .build()).getId();

        policyId = janitor.withClient(client).createPolicyDefinitionV2(PolicyDefinitionCreateDto.builder()
            .policyDefinitionId("policy-" + offset)
            .expression(UiPolicyExpression.builder()
                .type(UiPolicyExpressionType.EMPTY)
                .build())
            .build()).getId();
    }
}
