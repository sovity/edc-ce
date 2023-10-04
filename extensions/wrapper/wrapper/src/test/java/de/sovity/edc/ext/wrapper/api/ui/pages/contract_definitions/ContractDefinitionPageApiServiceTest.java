package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.ext.wrapper.TestUtils;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractDefinitionPageApiServiceTest {

    // arrange
    private EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void contractDefinitionPage(ContractDefinitionService contractDefinitionService) {
        var criterion = new Criterion("exampleLeft1", "=", "abc");
        createContractDefinition(contractDefinitionService, "contractDefinition-id-1", "contractPolicy-id-1", "accessPolicy-id-1", criterion);

        // act
        var result = client.uiApi().getContractDefinitionPage();

        // assert
        var contractDefinitions = result.getContractDefinitions();
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

    @Test
    void contractDefinitionPageSorting(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        createContractDefinition(
                contractDefinitionService,
                "contractDefinition-id-1",
                "contractPolicy-id-1",
                "accessPolicy-id-1",
                new Criterion("exampleLeft1", "=", "abc"),
                1628956800000L);
        createContractDefinition(
                contractDefinitionService,
                "contractDefinition-id-2",
                "contractPolicy-id-2",
                "accessPolicy-id-2",
                new Criterion("exampleLeft1", "=", "abc"),
                1628956801000L);
        createContractDefinition(
                contractDefinitionService,
                "contractDefinition-id-3",
                "contractPolicy-id-3",
                "accessPolicy-id-3",
                new Criterion("exampleLeft1", "=", "abc"),
                1628956802000L);

        // act
        var result = client.uiApi().getContractDefinitionPage();

        // assert
        assertThat(result.getContractDefinitions())
                .extracting(ContractDefinitionEntry::getContractPolicyId)
                .containsExactly("contractPolicy-id-3", "contractPolicy-id-2", "contractPolicy-id-1");

    }

    @Test
    void testContractDefinitionCreation(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criterion = new UiCriterion(
                "exampleLeft1",
                UiCriterionOperator.EQ,
                new UiCriterionLiteral(UiCriterionLiteralType.VALUE, "test", null));

        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId("contractDefinition-id-1")
                .contractPolicyId("contractPolicy-id-1")
                .accessPolicyId("accessPolicy-id-1")
                .assetSelector(List.of(criterion))
                .build();

        // act
        var response = client.uiApi().createContractDefinition(contractDefinition);

        // assert
        assertThat(response).isNotNull();
        var contractDefinitions = contractDefinitionService.query(QuerySpec.max()).getContent().toList();
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinitionEntry = contractDefinitions.get(0);
        assertThat(contractDefinitionEntry.getId()).isEqualTo("contractDefinition-id-1");
        assertThat(contractDefinitionEntry.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinitionEntry.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");

        var criterionEntry = contractDefinition.getAssetSelector().get(0);
        assertThat(criterionEntry.getOperandLeft()).isEqualTo("exampleLeft1");
        assertThat(criterionEntry.getOperator()).isEqualTo(UiCriterionOperator.EQ);
        assertThat(criterionEntry.getOperandRight().getType()).isEqualTo(UiCriterionLiteralType.VALUE);
        assertThat(criterionEntry.getOperandRight().getValue()).isEqualTo("test");
    }

    @Test
    void testDeleteContractDefinition(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criterion = new Criterion("exampleLeft1", "=", "exampleRight1");
        createContractDefinition(contractDefinitionService, "contractDefinition-id-1", "contractPolicy-id-1", "accessPolicy-id-1", criterion);
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
    }

    private void createContractDefinition(
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
    }
}
