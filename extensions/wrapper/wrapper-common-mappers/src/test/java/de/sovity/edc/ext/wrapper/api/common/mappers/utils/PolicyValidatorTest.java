package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.Prohibition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PolicyValidatorTest {
    @InjectMocks
    PolicyValidator policyValidator;

    @Test
    void testPolicy_null() {
        // arrange
        var errors = MappingErrors.root();
        var policy = (Policy) null;

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$: Policy is null");
    }

    @Test
    void testPolicy_ok() {
        // arrange
        var errors = MappingErrors.root();
        var policy = Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(mock(Permission.class))
                .build();

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void testPolicy_full() {
        // arrange
        var errors = MappingErrors.root();
        var policy = Policy.Builder.newInstance()
                .prohibition(mock(Prohibition.class))
                .duty(mock(Duty.class))
                .inheritsFrom("inheritsFrom")
                .assigner("assigner")
                .assignee("assignee")
                .target("target")
                .type(PolicyType.OFFER)
                .extensibleProperty("some", "prop")
                .build();

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
                "$: Policy has no permissions.",
                "$: Policy has prohibitions, which are currently unsupported.",
                "$: Policy has obligations, which are currently unsupported.",
                "$: Policy has inheritsFrom, which is currently unsupported.",
                "$: Policy has an assigner, which is currently unsupported.",
                "$: Policy has an assignee, which is currently unsupported.",
                "$: Policy has extensible properties.",
                "$: Policy does not have type SET, but OFFER, which is currently unsupported."
        );
    }

    @Test
    void testPermission_null() {
        // arrange
        var errors = MappingErrors.root();
        var permission = (Permission) null;

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$: Permission is null.");
    }

    @Test
    void testPermission_action_null() {
        // arrange
        var errors = MappingErrors.root();
        var permission = Permission.Builder.newInstance()
                .constraint(mock(Constraint.class))
                .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$.action: Action is null.");
    }

    @Test
    void testPermission_ok() {
        // arrange
        var errors = MappingErrors.root();
        var permission = Permission.Builder.newInstance()
                .constraint(mock(Constraint.class))
                .action(Action.Builder.newInstance().type("USE").build())
                .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void testPermission_full() {
        // arrange
        var errors = MappingErrors.root();
        var action = Action.Builder.newInstance()
                .type("idk")
                .constraint(mock(Constraint.class))
                .includedIn("includedIn")
                .build();
        var permission = Permission.Builder.newInstance()
                .duty(mock(Duty.class))
                .assigner("assigner")
                .assignee("assignee")
                .target("target")
                .action(action)
                .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
                "$: Permission has no constraints.",
                "$: Permission has duties, which is currently unsupported.",
                "$: Permission has an assigner, which is currently unsupported.",
                "$: Permission has an assignee, which is currently unsupported.",
                "$.action: Action has a type that is not 'USE', but 'idk'.",
                "$.action: Action has a value for includedIn, which is currently unsupported.",
                "$.action: Action has a constraint, which is currently unsupported."
        );
    }
}
