package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.AssetEntryDto;
import de.sovity.edc.client.gen.model.AtomicConstraintDto;
import de.sovity.edc.client.gen.model.ContractDefinitionRequestDto;
import de.sovity.edc.client.gen.model.CreateOfferingDto;
import de.sovity.edc.client.gen.model.CriterionDto;
import de.sovity.edc.client.gen.model.ExpressionDto;
import de.sovity.edc.client.gen.model.ExpressionType;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PermissionDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionRequestDto;
import de.sovity.edc.client.gen.model.PolicyDto;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
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
class CreateOfferingTest {
    EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void shouldCreateOffer() {
        // arrange
        var policyDefinitionRequest = PolicyDefinitionRequestDto.builder()
                .id("policy-def-1")
                .policy(PolicyDto.builder()
                        .permission(PermissionDto.builder()
                                .constraints(ExpressionDto.builder()
                                        .type(ExpressionType.ATOMIC_CONSTRAINT)
                                        .atomicConstraint(AtomicConstraintDto.builder()
                                                .leftExpression("ALWAYS_TRUE")
                                                .operator(OperatorDto.EQ)
                                                .rightExpression("true")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        var assetRequest = AssetEntryDto.builder()
                .assetRequestId("asset-1")
                .assetRequestProperties(Map.of(Prop.Dcterms.TITLE, "Asset 1"))
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.BASE_URL, "https://my-data-source"
                ))
                .build();

        var contractDefinitionRequest = ContractDefinitionRequestDto.builder()
                .id("contract-def-1")
                .contractPolicyId("policy-def-1")
                .accessPolicyId("policy-def-1")
                .assetsSelector(List.of(CriterionDto.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator("=")
                        .operandRight("asset-1")
                        .build()))
                .build();

        var request = CreateOfferingDto.builder()
                .assetEntry(assetRequest)
                .policyDefinitionRequest(policyDefinitionRequest)
                .contractDefinitionRequest(contractDefinitionRequest)
                .build();

        // act
        client.useCaseApi().createOffer(request);
        var dataOffers = client.uiApi().getCatalogPageDataOffers(TestUtils.PROTOCOL_ENDPOINT);

        // assert
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);

        assertThat(dataOffer.getAsset().getAssetId()).isEqualTo("asset-1");
        assertThat(dataOffer.getAsset().getTitle()).isEqualTo("Asset 1");
        assertThat(dataOffer.getContractOffers()).hasSize(1);

        var contractOffer = dataOffer.getContractOffers().get(0);
        assertThat(contractOffer.getPolicy().getConstraints()).hasSize(1);

        var constraint = contractOffer.getPolicy().getConstraints().get(0);
        assertThat(constraint.getLeft()).isEqualTo("ALWAYS_TRUE");
        assertThat(constraint.getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(constraint.getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(constraint.getRight().getValue()).isEqualTo("true");
    }
}
