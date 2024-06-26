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

package de.sovity.edc.ext.wrapper.api.ui.pages.catalog;

import de.sovity.edc.client.EdcClient;
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
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ApiTest
@ExtendWith(EdcExtension.class)
class CatalogApiTest {
    private EdcClient client;
    private final String dataOfferId = "my-data-offer-2023-11";


    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    /**
     * There used to be issues with the Prop.DISTRIBUTION field being occupied by core EDC.
     * This test verifies that the field can be used by us.
     */
    @DisabledOnGithub
    @Test
    void testDistributionKey() {
        // arrange
        createAsset();
        createPolicy();
        createContractDefinition();
        // act
        var catalogPageDataOffers = client.uiApi().getCatalogPageDataOffers(TestUtils.PROTOCOL_ENDPOINT);

        // assert
        assertThat(catalogPageDataOffers.size()).isEqualTo(1);
        assertThat(catalogPageDataOffers.get(0).getAsset().getTitle()).isEqualTo("My Data Offer");
        assertThat(catalogPageDataOffers.get(0).getAsset().getMediaType()).isEqualTo("Media Type");
    }

    private void createAsset() {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://a")
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
                .mediaType("Media Type")
                .dataSource(dataSource)
                .build();

        client.uiApi().createAsset(asset);
    }

    private void createPolicy() {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId(dataOfferId)
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build();

        client.uiApi().createPolicyDefinition(policyDefinition);
    }

    private void createContractDefinition() {
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

        client.uiApi().createContractDefinition(contractDefinition);
    }

}
