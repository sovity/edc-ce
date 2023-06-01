package de.sovity.edc.ext.wrapper.api.usecase.services;

import de.sovity.edc.ext.wrapper.api.common.model.ConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyMappingServiceTest {

    private final PolicyMappingService mappingService = new PolicyMappingService();

    @Test
    void policyDtoToPolicy_noConstraints_returnPolicy() {
        // arrange
        var dto = PolicyDto.builder().build();

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).isEmpty();
    }

    @Test
    void policyDtoToPolicy_atomicConstraint_returnPolicy() {
        // arrange
        var constraint = new ConstraintDto("left", OperatorDto.EQ, "right");
        var dto = PolicyDto.builder()
                .permission(PermissionDto.builder()
                        .constraints(new ExpressionDto(constraint, null, null, null))
                        .build())
                .build();

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getConstraints().get(0)).isInstanceOf(AtomicConstraint.class);
    }

    @Test
    void policyDtoToPolicy_andConstraint_returnPolicy() {
        // arrange
        var constraint1 = new ConstraintDto("left1", OperatorDto.EQ, "right1");
        var expression1 = new ExpressionDto(constraint1, null, null, null);
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var expression2 = new ExpressionDto(constraint2, null, null, null);
        var dto = PolicyDto.builder()
                .permission(PermissionDto.builder()
                        .constraints(new ExpressionDto(null, List.of(expression1, expression2), null, null))
                        .build())
                .build();

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getConstraints().get(0)).isInstanceOf(AndConstraint.class);

        var andConstraint = (AndConstraint) permission.getConstraints().get(0);
        assertThat(andConstraint.getConstraints()).hasSize(2);
        andConstraint.getConstraints().forEach(this::assertAtomicConstraint);
    }

    @Test
    void policyDtoToPolicy_orConstraint_returnPolicy() {
        // arrange
        var constraint1 = new ConstraintDto("left1", OperatorDto.EQ, "right1");
        var expression1 = new ExpressionDto(constraint1, null, null, null);
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var expression2 = new ExpressionDto(constraint2, null, null, null);
        var dto = PolicyDto.builder()
                .permission(PermissionDto.builder()
                        .constraints(new ExpressionDto(null, null, List.of(expression1, expression2), null))
                        .build())
                .build();

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getConstraints().get(0)).isInstanceOf(OrConstraint.class);

        var orConstraint = (OrConstraint) permission.getConstraints().get(0);
        assertThat(orConstraint.getConstraints()).hasSize(2);
        orConstraint.getConstraints().forEach(this::assertAtomicConstraint);
    }

    @Test
    void policyDtoToPolicy_xorConstraint_returnPolicy() {
        // arrange
        var constraint1 = new ConstraintDto("left1", OperatorDto.EQ, "right1");
        var expression1 = new ExpressionDto(constraint1, null, null, null);
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var expression2 = new ExpressionDto(constraint2, null, null, null);
        var dto = PolicyDto.builder()
                .permission(PermissionDto.builder()
                        .constraints(new ExpressionDto(null, null, null, List.of(expression1, expression2)))
                        .build())
                .build();

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getConstraints().get(0)).isInstanceOf(XoneConstraint.class);

        var xoneConstraint = (XoneConstraint) permission.getConstraints().get(0);
        assertThat(xoneConstraint.getConstraints()).hasSize(2);
        xoneConstraint.getConstraints().forEach(this::assertAtomicConstraint);
    }

    void assertPolicyAttributes(Policy policy) {
        assertThat(policy.getType()).isEqualByComparingTo(PolicyType.SET);
        assertThat(policy.getPermissions()).hasSize(1);
        assertThat(policy.getProhibitions()).isEmpty();
        assertThat(policy.getObligations()).isEmpty();
    }

    void assertAtomicConstraint(Constraint constraint) {
        assertThat(constraint).isInstanceOf(AtomicConstraint.class);

        var atomic = (AtomicConstraint) constraint;
        assertThat(((LiteralExpression) atomic.getLeftExpression()).getValue().toString()).isIn("left1", "left2");
        assertThat(atomic.getOperator()).isEqualTo(Operator.EQ);
        assertThat(((LiteralExpression) atomic.getRightExpression()).getValue().toString()).isIn("right1", "right2");
    }
}
