/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
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
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.e2e.utils.Consumer;
import de.sovity.edc.e2e.utils.E2eTestExtension;
import de.sovity.edc.e2e.utils.Provider;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(E2eTestExtension.class)
class UseCaseApiWrapperTest {

    private MockDataAddressRemote dataAddress;
    private final String dataOfferData = "expected data 123";

    private final String dataOfferId = "my-data-offer-2023-11";

    @BeforeEach
    void setup(@Provider ConnectorRemote providerConnector) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @DisabledOnGithub
    @Test
    void catalog_filtering_by_like(@Consumer EdcClient consumerClient, @Provider ConnectorRemote providerConnector, @Provider EdcClient providerClient) {
        // arrange
        createPolicy(providerClient);
        createAsset(providerClient);
        createContractDefinition(providerClient);

        var query = criterion(providerConnector, Prop.Edc.ID, CatalogFilterExpressionOperator.LIKE, "%data-offer%");

        // act
        var dataOffers = consumerClient.useCaseApi().queryCatalog(query);

        // assert
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAsset().getAssetId()).isEqualTo(dataOfferId);
        assertThat(dataOffers.get(0).getAsset().getTitle()).isEqualTo("My Data Offer");

    }

    private CatalogQuery criterion(
        ConnectorRemote providerConnector,
        String leftOperand,
        CatalogFilterExpressionOperator operator,
        String rightOperand) {

        return CatalogQuery.builder()
                .connectorEndpoint(getProtocolEndpoint(providerConnector))
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

    private void createAsset(EdcClient providerClient) {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(dataOfferData))
                .build())
            .build();

        var asset = UiAssetCreateRequest.builder()
            .id(dataOfferId)
            .title("My Data Offer")
            .description("Example Data Offer.")
            .version("2023-11")
            .language("EN")
            .publisherHomepage("https://my-department.my-org.com/my-data-offer")
            .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
            .dataSource(dataSource)
            .build();

        providerClient.uiApi().createAsset(asset);
    }

    private void createPolicy(EdcClient providerClient) {
        var afterYesterday = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().minusDays(1).toString())
                        .build())
                .build();

        var beforeTomorrow = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.LT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().plusDays(1).toString())
                        .build())
                .build();

        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId(dataOfferId)
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of(afterYesterday, beforeTomorrow))
                        .build())
                .build();

        providerClient.uiApi().createPolicyDefinition(policyDefinition);
    }

    private void createContractDefinition(EdcClient providerClient) {
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId(dataOfferId)
                .accessPolicyId(dataOfferId)
                .contractPolicyId(dataOfferId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build())
                        .build()))
                .build();

        providerClient.uiApi().createContractDefinition(contractDefinition);
    }

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
