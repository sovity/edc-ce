package de.sovity.edc.ext.wrapper.api.offering.services;

import java.util.List;

import de.sovity.edc.ext.wrapper.api.common.model.ConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyTypeDto;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyMappingServiceTest {

    private final PolicyMappingService mappingService = new PolicyMappingService();

    @Test
    void policyDtoToPolicy_noConstraints_returnPolicy() {
        // arrange
        var dto = new PolicyDto(PolicyTypeDto.SET, new PermissionDto("USE"));

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
        var dto = new PolicyDto(PolicyTypeDto.SET, PermissionDto.Builder.newInstance()
                .action("USE")
                .constraint(constraint)
                .build());

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
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var dto = new PolicyDto(PolicyTypeDto.SET, PermissionDto.Builder.newInstance()
                .action("USE")
                .andConstraint(List.of(constraint1, constraint2))
                .build());

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
    }

    @Test
    void policyDtoToPolicy_orConstraint_returnPolicy() {
        // arrange
        var constraint1 = new ConstraintDto("left1", OperatorDto.EQ, "right1");
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var dto = new PolicyDto(PolicyTypeDto.SET, PermissionDto.Builder.newInstance()
                .action("USE")
                .orConstraint(List.of(constraint1, constraint2))
                .build());

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
    }

    @Test
    void policyDtoToPolicy_allConstraintTypes_returnPolicy() {
        // arrange
        var constraint1 = new ConstraintDto("left1", OperatorDto.EQ, "right1");
        var constraint2 = new ConstraintDto("left2", OperatorDto.EQ, "right2");
        var dto = new PolicyDto(PolicyTypeDto.SET, PermissionDto.Builder.newInstance()
                .action("USE")
                .constraint(constraint1)
                .andConstraint(List.of(constraint1, constraint2))
                .orConstraint(List.of(constraint1, constraint2))
                .build());

        // act
        var policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertPolicyAttributes(policy);

        var permission = policy.getPermissions().get(0);
        assertThat(permission.getAction().getType()).isEqualTo("USE");
        assertThat(permission.getConstraints()).hasSize(3);

        assertThat(permission.getConstraints().get(0)).isInstanceOf(AtomicConstraint.class);

        assertThat(permission.getConstraints().get(1)).isInstanceOf(AndConstraint.class);
        var andConstraint = (AndConstraint) permission.getConstraints().get(1);
        assertThat(andConstraint.getConstraints()).hasSize(2);

        assertThat(permission.getConstraints().get(2)).isInstanceOf(OrConstraint.class);
        var orConstraint = (OrConstraint) permission.getConstraints().get(2);
        assertThat(orConstraint.getConstraints()).hasSize(2);
    }

    void assertPolicyAttributes(Policy policy) {
        assertThat(policy.getType()).isEqualByComparingTo(PolicyType.SET);
        assertThat(policy.getPermissions()).hasSize(1);
        assertThat(policy.getProhibitions()).isEmpty();
        assertThat(policy.getObligations()).isEmpty();
    }
}
