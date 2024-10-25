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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.e2e.WrapperApiUtils;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.extension.e2e.junit.Janitor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContractDefinitionPageApiServiceTest {

    @RegisterExtension
    static CeIntegrationTestExtension providerExtension = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @Test
    void contractDefinitionPage(EdcClient client, ContractDefinitionService contractDefinitionService, Janitor janitor) {
        // arrange

        var criterion = new Criterion("exampleLeft1", "=", "abc");
        createContractDefinition(janitor, contractDefinitionService, "contractDefinition-id-1", "contractPolicy-id-1", "accessPolicy-id-1",
            criterion);

        // act
        var result = client.uiApi().getContractDefinitionPage();

        // assert
        var contractDefinitions = WrapperApiUtils.getContractDefinitionWithId(client, "contractDefinition-id-1");
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinition = contractDefinitions.get(0);
        assertThat(contractDefinition.getContractDefinitionId()).isEqualTo("contractDefinition-id-1");
        assertThat(contractDefinition.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinition.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");
        assertThat(contractDefinition.getAssetSelector()).hasSize(1);

        var criterionEntry = contractDefinition.getAssetSelector().get(0);
        assertThat(criterionEntry.getOperandLeft()).isEqualTo("exampleLeft1");
        assertThat(criterionEntry.getOperator()).isEqualTo(UiCriterionOperator.EQ);
        assertThat(criterionEntry.getOperandRight().getType()).isEqualTo(UiCriterionLiteralType.VALUE);
        assertThat(criterionEntry.getOperandRight().getValue()).isEqualTo("abc");
    }

    @Order(1)
    @Test
    void contractDefinitionPageSorting(EdcClient client, ContractDefinitionService contractDefinitionService, Janitor janitor) {
        // arrange

        createContractDefinition(
            janitor,
            contractDefinitionService,
            "contractDefinition-id-1",
            "contractPolicy-id-1",
            "accessPolicy-id-1",
            new Criterion("exampleLeft1", "=", "abc"),
            1628000000000L);

        createContractDefinition(
            janitor,
            contractDefinitionService,
            "contractDefinition-id-2",
            "contractPolicy-id-2",
            "accessPolicy-id-2",
            new Criterion("exampleLeft1", "=", "abc"),
            1628000001000L);

        createContractDefinition(
            janitor,
            contractDefinitionService,
            "contractDefinition-id-3",
            "contractPolicy-id-3",
            "accessPolicy-id-3",
            new Criterion("exampleLeft1", "=", "abc"),
            1628000002000L);

        // act
        var result = client.uiApi().getContractDefinitionPage();


        // assert
        assertThat(result.getContractDefinitions())
            .extracting(ContractDefinitionEntry::getContractPolicyId)
            .containsExactly("contractPolicy-id-3", "contractPolicy-id-2", "contractPolicy-id-1");

    }

    @Test
    void testContractDefinitionCreation(EdcClient client, ContractDefinitionService contractDefinitionService) {
        // arrange

        var criterion = new UiCriterion(
            "exampleLeft1",
            UiCriterionOperator.EQ,
            new UiCriterionLiteral(UiCriterionLiteralType.VALUE, "test", null));

        var contractDefinition = ContractDefinitionRequest.builder()
            .contractDefinitionId("contractDefinition-id-10")
            .contractPolicyId("contractPolicy-id-10")
            .accessPolicyId("accessPolicy-id-10")
            .assetSelector(List.of(criterion))
            .build();

        // act
        var response = client.uiApi().createContractDefinition(contractDefinition);

        // assert
        assertThat(response).isNotNull();

        var selector = QuerySpec.Builder.newInstance()
            .filter(Criterion.criterion("id", "=", "contractDefinition-id-10"))
            .build();

        var contractDefinitions = contractDefinitionService.query(selector).getContent().toList();
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinitionEntry = contractDefinitions.get(0);
        assertThat(contractDefinitionEntry.getId()).isEqualTo("contractDefinition-id-10");
        assertThat(contractDefinitionEntry.getContractPolicyId()).isEqualTo("contractPolicy-id-10");
        assertThat(contractDefinitionEntry.getAccessPolicyId()).isEqualTo("accessPolicy-id-10");

        var criterionEntry = contractDefinition.getAssetSelector().get(0);
        assertThat(criterionEntry.getOperandLeft()).isEqualTo("exampleLeft1");
        assertThat(criterionEntry.getOperator()).isEqualTo(UiCriterionOperator.EQ);
        assertThat(criterionEntry.getOperandRight().getType()).isEqualTo(UiCriterionLiteralType.VALUE);
        assertThat(criterionEntry.getOperandRight().getValue()).isEqualTo("test");
    }

    @Test
    void testDeleteContractDefinition(EdcClient client, ContractDefinitionService contractDefinitionService, Janitor janitor) {
        // arrange

        val criterion = new Criterion("exampleLeft1", "=", "exampleRight1");
        createContractDefinition(
            janitor,
            contractDefinitionService,
            "contractDefinition-id-20",
            "contractPolicy-id-20",
            "accessPolicy-id-20",
            criterion);

        val selectId20 = QuerySpec.Builder.newInstance()
            .filter(Criterion.criterion("id", "=", "contractDefinition-id-20"))
            .build();

        val contractDefinitions = contractDefinitionService.query(selectId20).getContent().toList();

        assertThat(contractDefinitions).hasSize(1);

        val contractDefinition = contractDefinitions.get(0);

        // act
        val response = client.uiApi().deleteContractDefinition(contractDefinition.getId());

        // assert
        assertThat(response.getId()).isEqualTo(contractDefinition.getId());
        assertThat(contractDefinitionService.query(selectId20).getContent()).isEmpty();
    }

    private void createContractDefinition(
        Janitor janitor,
        ContractDefinitionService contractDefinitionService,
        String contractDefinitionId,
        String contractPolicyId,
        String accessPolicyId,
        Criterion criteria,
        long createdAt
    ) {
        var contractDefinition = ContractDefinition.Builder.newInstance()
            .id(contractDefinitionId)
            .contractPolicyId(contractPolicyId)
            .accessPolicyId(accessPolicyId)
            .assetsSelector(List.of(criteria))
            .createdAt(createdAt)
            .build();

        contractDefinitionService.create(contractDefinition);

        janitor.afterEach(() -> contractDefinitionService.delete(contractDefinitionId));
    }

    private void createContractDefinition(
        Janitor janitor,
        ContractDefinitionService contractDefinitionService,
        String contractDefinitionId,
        String contractPolicyId,
        String accessPolicyId,
        Criterion criteria
    ) {
        var contractDefinition = ContractDefinition.Builder.newInstance()
            .id(contractDefinitionId)
            .contractPolicyId(contractPolicyId)
            .accessPolicyId(accessPolicyId)
            .assetsSelector(List.of(criteria))
            .build();

        contractDefinitionService.create(contractDefinition);

        janitor.afterEach(() -> contractDefinitionService.delete(contractDefinitionId));
    }
}
