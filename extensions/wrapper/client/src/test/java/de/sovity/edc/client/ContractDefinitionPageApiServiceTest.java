package de.sovity.edc.client;

import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.UiCriterionDto;
import de.sovity.edc.client.gen.model.UiCriterionLiteralDto;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.CriterionMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.OperatorMapper;
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
    OperatorMapper operatorMapper;
    CriterionMapper criterionMapper;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        operatorMapper = new OperatorMapper();
        criterionMapper = new CriterionMapper(operatorMapper);
    }

    @Test
    void contractDefinitionPage(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criterion = new Criterion("exampleLeft1", "EQ", "abc");
        createContractDefinition(contractDefinitionService, "contractPolicy-id-1", "accessPolicy-id-1", criterion);

        // act
        var result = client.uiApi().contractDefinitionPage();

        // assert
        var contractDefinitions = result.getContractDefinitions();
        assertThat(contractDefinitions).hasSize(1);
        var contractDefinition = contractDefinitions.get(0);
        assertThat(contractDefinition.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinition.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");
        assertThat(contractDefinition.getAssetSelector()).hasSize(1);

        var criterionEntry = contractDefinition.getAssetSelector().get(0);
        assertThat(criterionEntry.getOperandLeft()).isEqualTo("exampleLeft1");
        assertThat(criterionEntry.getOperator()).isEqualTo(UiCriterionDto.OperatorEnum.EQ);
        assertThat(criterionEntry.getOperandRight().getType()).isEqualTo(UiCriterionLiteralDto.TypeEnum.VALUE);
        assertThat(criterionEntry.getOperandRight().getValue()).isEqualTo("abc");
    }

    @Test
    void contractDefinitionPageSorting(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        createContractDefinition(
                contractDefinitionService,
                "contractPolicy-id-1",
                "accessPolicy-id-1",
                new Criterion("exampleLeft1", "EQ", "abc"));
        createContractDefinition(
                contractDefinitionService,
                "contractPolicy-id-2",
                "accessPolicy-id-2",
                new Criterion("exampleLeft1", "EQ", "abc"));
        TestUtils.wait(1);
        createContractDefinition(
                contractDefinitionService,
                "contractPolicy-id-3",
                "accessPolicy-id-3",
                new Criterion("exampleLeft1", "EQ", "abc"));

        // act
        var result = client.uiApi().contractDefinitionPage();

        // assert
        assertThat(result.getContractDefinitions())
                .extracting(ContractDefinitionEntry::getContractPolicyId)
                .containsExactly("contractPolicy-id-3", "contractPolicy-id-2", "contractPolicy-id-1");
    }

    @Test
    void testContractDefinitionCreation(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criterion = new UiCriterionDto(
                "exampleLeft1",
                UiCriterionDto.OperatorEnum.EQ,
                new UiCriterionLiteralDto(UiCriterionLiteralDto.TypeEnum.VALUE, "test", null));

        var contractDefinition = ContractDefinitionRequest.builder()
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
        assertThat(contractDefinitionEntry.getContractPolicyId()).isEqualTo("contractPolicy-id-1");
        assertThat(contractDefinitionEntry.getAccessPolicyId()).isEqualTo("accessPolicy-id-1");

        var criterionEntry = contractDefinition.getAssetSelector().get(0);
        assertThat(criterionEntry.getOperandLeft()).isEqualTo("exampleLeft1");
        assertThat(criterionEntry.getOperator()).isEqualTo(UiCriterionDto.OperatorEnum.EQ);
        assertThat(criterionEntry.getOperandRight().getType()).isEqualTo(UiCriterionLiteralDto.TypeEnum.VALUE);
        assertThat(criterionEntry.getOperandRight().getValue()).isEqualTo("test");
    }

    @Test
    void testDeleteContractDefinition(ContractDefinitionService contractDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var criterion = new Criterion("exampleLeft1", "EQ", "exampleRight1");
        createContractDefinition(contractDefinitionService, "contractPolicy-id-1", "accessPolicy-id-1", criterion);
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
            Criterion criteria
    ) {
        var contractDefinition = ContractDefinition.Builder.newInstance()
                .contractPolicyId(contractPolicyId)
                .accessPolicyId(accessPolicyId)
                .assetsSelector(List.of(criteria))
                .build();
        contractDefinitionService.create(contractDefinition);
    }
}
