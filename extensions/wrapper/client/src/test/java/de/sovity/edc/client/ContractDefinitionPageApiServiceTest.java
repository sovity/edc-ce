/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.client;

import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.CriterionDto;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractDefinitionPageApiServiceTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
    }

    @Test
    void contractDefinitionPage(ContractDefinitionService contractDefinitionService) {
        //arrange
        var client = TestUtils.edcClient();
        var criteria = Arrays.asList(
                new Criterion(
                        "exampleLeft1",
                        "operator1",
                        "exampleRight1")
        );
        createContractDefinition(contractDefinitionService, "contractPolicy-id-1", "accessPolicy-id-1", criteria);

        // act
        var result = client.uiApi().contractDefinitionPage();

        //assert
        var contractDefinitions = result.getContractDefinitions();
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinition = contractDefinitions.get(0);
        assertThat(contractDefinition.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinition.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");
        assertThat(contractDefinition.getCriteria().get(0).getOperandLeft()).isEqualTo(criteria.get(0).getOperandLeft());
        assertThat(contractDefinition.getCriteria().get(0).getOperator()).isEqualTo(criteria.get(0).getOperator());
        assertThat(contractDefinition.getCriteria().get(0).getOperandRight()).isEqualTo(criteria.get(0).getOperandRight());
    }

    @Test
    void contractDefinitionPageSorting(ContractDefinitionService contractDefinitionService) {
        //arrange
        var client = TestUtils.edcClient();
        createContractDefinition(contractDefinitionService, "contractPolicy-id-1", "accessPolicy-id-1", Arrays.asList());
        TestUtils.wait(1);
        createContractDefinition(contractDefinitionService, "contractPolicy-id-2", "accessPolicy-id-2", Arrays.asList());
        TestUtils.wait(1);
        createContractDefinition(contractDefinitionService, "contractPolicy-id-3", "accessPolicy-id-3", Arrays.asList());

        // act
        var result = client.uiApi().contractDefinitionPage();

        //assert
        assertThat(result.getContractDefinitions())
                .extracting(contractDefinition -> contractDefinition.getContractPolicyId())
                .containsExactly("contractPolicy-id-3", "contractPolicy-id-2", "contractPolicy-id-1");

    }

    @Test
    void testContractDefinitionCreation(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criteria = Arrays.asList(
                new CriterionDto(
                        "exampleLeft1",
                        "operator1",
                        "exampleRight1")
        );
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractPolicyId("contractPolicy-id-1")
                .accessPolicyId("accessPolicy-id-1")
                .assetsSelector(criteria)
                .build();

        // act
        var response = client.uiApi().createContractDefinition(contractDefinition);

        //assert
        assertThat(response).isNotNull();
        var contractDefinitions = contractDefinitionService.query(QuerySpec.max()).getContent().toList();
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinitionEntry = contractDefinitions.get(0);
        assertThat(contractDefinitionEntry.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinitionEntry.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");
        assertThat(contractDefinitionEntry.getAssetsSelector().get(0).getOperandLeft()).isEqualTo(criteria.get(0).getOperandLeft());
        assertThat(contractDefinitionEntry.getAssetsSelector().get(0).getOperator()).isEqualTo(criteria.get(0).getOperator());
        assertThat(contractDefinitionEntry.getAssetsSelector().get(0).getOperandRight()).isEqualTo(criteria.get(0).getOperandRight());
    }

    @Test
    void testDeleteContractDefinition(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criteria = Arrays.asList(
                new Criterion(
                        "exampleLeft1",
                        "operator1",
                        "exampleRight1")
        );
        createContractDefinition(contractDefinitionService, "contractPolicy-id-1", "accessPolicy-id-1", criteria);
        assertThat(contractDefinitionService.query(QuerySpec.max()).getContent().toList()).hasSize(1);
        var contractDefinition = contractDefinitionService.query(QuerySpec.max()).getContent().toList().get(0);

        // act
        var response = client.uiApi().deleteContractDefinition(contractDefinition.getId());

        // assert
        assertThat(response.getId()).isEqualTo(contractDefinition.getId());
        assertThat(contractDefinitionService.query(QuerySpec.max()).getContent()).isEmpty();
    }

    private void createContractDefinition(
            ContractDefinitionService contractDefinitionService,
            String contractPolicyId,
            String accessPolicyId,
            List<Criterion> criteria
    ) {
        var contractDefinition = ContractDefinition.Builder.newInstance()
                .contractPolicyId(contractPolicyId)
                .accessPolicyId(accessPolicyId)
                .assetsSelector(criteria)
                .build();
        contractDefinitionService.create(contractDefinition);
    }
}
