package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.AtomicConstraintDto;
import de.sovity.edc.client.gen.model.Expression;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PermissionDto;
import de.sovity.edc.client.gen.model.PolicyCreateRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.sovity.edc.client.gen.model.ExpressionType.AND;
import static de.sovity.edc.client.gen.model.ExpressionType.ATOMIC_CONSTRAINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ApiTest
@ExtendWith(EdcExtension.class)
public class PolicyDefinitionApiServiceTest {

    private EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void createTraceXPolicy() {
        // arrange
        var policyId = UUID.randomUUID().toString();
        var membershipElement = buildAtomicElement("Membership", OperatorDto.EQ, "active");
        var purposeElement = buildAtomicElement("PURPOSE", OperatorDto.EQ, "ID 3.1 Trace");
        var andElement = new Expression()
                .expressionType(AND)
                .expressions(List.of(membershipElement, purposeElement));
        var permissionDto = new PermissionDto(andElement);
        var createRequest = new PolicyCreateRequest(policyId, permissionDto);

        // act
        var response = client.useCaseApi().createPolicyDefinitionUseCase(createRequest);

        // assert
        assertThat(response.getId()).isEqualTo(policyId);
        var policyById = getPolicyById(policyId);
        assertThat(policyById).isPresent();
        var policyDefinitionDto = policyById.get();
        assertEquals(policyId, policyDefinitionDto.getPolicyDefinitionId());
        assertPolicyJsonLd(policyDefinitionDto);
    }

    private void assertPolicyJsonLd(PolicyDefinitionDto policyDefinitionDto) {
        var permission = getPermissionJsonObject(policyDefinitionDto.getPolicy().getPolicyJsonLd());
        var action = permission.get(Prop.Odrl.ACTION);
        assertEquals(Prop.Odrl.USE, action.asJsonObject().getString(Prop.Odrl.TYPE));

        var permissionConstraints = permission.get(Prop.Odrl.CONSTRAINT).asJsonArray();
        assertThat(permissionConstraints).hasSize(1);
        var andConstraint = permissionConstraints.get(0).asJsonObject();
        var andConstraints = andConstraint.get(Prop.Odrl.AND).asJsonArray();
        assertThat(andConstraints).hasSize(2);

        var membershipConstraint = andConstraints.get(0).asJsonObject();
        var purposeConstraint = andConstraints.get(1).asJsonObject();
        assertAtomicConstraint(membershipConstraint, "Membership", "active");
        assertAtomicConstraint(purposeConstraint, "PURPOSE", "ID 3.1 Trace");
    }

    private static JsonObject getPermissionJsonObject(String policyJsonLdString) {
        var jsonReader = Json.createReader(new StringReader(policyJsonLdString));
        var jsonObject = jsonReader.readObject();
        var permissionList = jsonObject.get(Prop.Odrl.PERMISSION);
        return permissionList.asJsonArray().get(0).asJsonObject();
    }

    private void assertAtomicConstraint(JsonObject atomicConstraint, String left, String right) {
        var leftOperand = atomicConstraint.getJsonObject(Prop.Odrl.LEFT_OPERAND);
        assertEquals(left, leftOperand.getString("@value"));
        var rightOperand = atomicConstraint.getJsonObject(Prop.Odrl.RIGHT_OPERAND);
        assertEquals(right, rightOperand.getString("@value"));
    }

    private Expression buildAtomicElement(
            String left,
            OperatorDto operator,
            String right) {
        var atomicConstraint = new AtomicConstraintDto()
                .leftExpression(left)
                .operator(operator)
                .rightExpression(right);
        return new Expression()
                .expressionType(ATOMIC_CONSTRAINT)
                .atomicConstraint(atomicConstraint);
    }

    private Optional<PolicyDefinitionDto> getPolicyById(String policyId) {
        var policyDefinitionsResponse = client.uiApi().getPolicyDefinitionPage();
        return policyDefinitionsResponse.getPolicies().stream()
                .filter(policy -> policy.getPolicyDefinitionId().equals(policyId))
                .findFirst();
    }
}
