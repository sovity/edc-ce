/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.common.mappers.policy;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpressionExtractorTest {
    @InjectMocks
    ExpressionExtractor expressionExtractor;

    @Mock
    PolicyValidator policyValidator;

    @Mock
    ExpressionMapper expressionMapper;

    @Test
    void test_getPermissionConstraints_null() {
        // arrange
        var policy = Policy.Builder.newInstance().build();
        var errors = MappingErrors.root();

        // act
        var actual = expressionExtractor.getPermissionExpressions(policy, errors);

        // assert
        assertThat(actual).isEmpty();
        verify(policyValidator).validateOtherPolicyFieldsUnset(policy, errors);
    }

    @Test
    void test_getPermissionConstraints_no_constraints() {
        // arrange
        var permission = Permission.Builder.newInstance()
            .build();
        var policy = Policy.Builder.newInstance()
            .permissions(List.of(permission))
            .build();
        var errors = MappingErrors.root();

        // act
        var actual = expressionExtractor.getPermissionExpressions(policy, errors);

        // assert
        assertThat(actual).isEmpty();
        verify(policyValidator).validateOtherPolicyFieldsUnset(policy, errors);
    }

    @Test
    void test_getPermissionConstraints_merge_constraints() {
        // arrange
        var first = mock(Constraint.class);
        var firstPermission = Permission.Builder.newInstance()
            .constraint(first)
            .build();

        var other = mock(Constraint.class);
        var otherPermission = Permission.Builder.newInstance()
            .constraint(other)
            .build();

        var policy = Policy.Builder.newInstance()
            .permission(firstPermission)
            .permission(otherPermission)
            .build();
        var errors = MappingErrors.root();

        var firstUiExpression = mock(UiPolicyExpression.class);
        var otherUiExpression = mock(UiPolicyExpression.class);
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(first)), any())).thenReturn(List.of(firstUiExpression));
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(other)), any())).thenReturn(List.of(otherUiExpression));

        // act
        var actual = expressionExtractor.getPermissionExpressions(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(firstPermission), any());
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(otherPermission), any());
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(firstUiExpression, otherUiExpression);
    }

    @Test
    void test_getPermissionConstraints_error_mapping() {
        // arrange
        var constraint = mock(Constraint.class);
        var permission = Permission.Builder.newInstance()
            .constraint(constraint)
            .build();

        var policy = Policy.Builder.newInstance()
            .permission(permission)
            .build();
        var errors = MappingErrors.root();

        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(constraint)), any())).thenAnswer(i -> {
            i.getArgument(1, MappingErrors.class).add("test");
            return List.of();
        });

        // act
        var actual = expressionExtractor.getPermissionExpressions(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(permission), any());
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
            "$.permissions[0].constraints: test"
        );
    }
}
