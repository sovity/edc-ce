package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstraintExtractorTest {
    @InjectMocks
    ConstraintExtractor constraintExtractor;

    @Mock
    PolicyValidator policyValidator;

    @Mock
    AtomicConstraintMapper atomicConstraintMapper;

    @Test
    void test_getPermissionConstraints_null() {
        // arrange
        var policy = Policy.Builder.newInstance().build();
        var errors = MappingErrors.root();

        // act
        var actual = constraintExtractor.getPermissionConstraints(policy, errors);

        // assert
        assertThat(actual).isEmpty();
        verify(policyValidator).validateOtherPolicyFieldsUnset(policy, errors);
    }

    @Test
    void test_getPermissionConstraints_many_constraints() {
        // arrange
        var first = mock(AtomicConstraint.class);
        var other = mock(AtomicConstraint.class);
        var permission = Permission.Builder.newInstance()
                .constraint(null)
                .constraint(first)
                .constraint(other)
                .constraint(mock(AndConstraint.class))
                .constraint(mock(OrConstraint.class))
                .constraint(mock(XoneConstraint.class))
                .build();
        var policy = Policy.Builder.newInstance()
                .permission(null)
                .permission(permission)
                .permission(Permission.Builder.newInstance().build())
                .build();
        var errors = MappingErrors.root();

        var expected = mock(UiPolicyConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(first), any())).thenReturn(Optional.of(expected));
        when(atomicConstraintMapper.buildUiConstraint(same(other), any())).thenReturn(Optional.empty());

        // act
        var actual = constraintExtractor.getPermissionConstraints(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(permission), any());
        verify(policyValidator).validateOtherPermissionFieldsUnset(eq(null), any());
        assertThat(actual).containsExactly(expected);
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
                "$.permissions[1].constraints[0]: Constraint is null.",
                "$.permissions[1].constraints[3]: AndConstraints are currently unsupported.",
                "$.permissions[1].constraints[4]: OrConstraints are currently unsupported.",
                "$.permissions[1].constraints[5]: XoneConstraints are currently unsupported."
        );
    }
}
