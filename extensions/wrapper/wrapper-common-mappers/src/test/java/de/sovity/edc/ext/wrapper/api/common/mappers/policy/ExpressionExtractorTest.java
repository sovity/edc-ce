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
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpressionType;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        assertThat(actual.getType()).isEqualTo(UiPolicyExpressionType.EMPTY);
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
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        assertThat(actual.getType()).isEqualTo(UiPolicyExpressionType.EMPTY);
        verify(policyValidator).validateOtherPolicyFieldsUnset(policy, errors);
    }

    @Test
    void test_getPermissionConstraints_single_constraint() {
        // arrange
        var constraint = mock(Constraint.class);
        var permission = Permission.Builder.newInstance()
            .constraint(constraint)
            .build();

        var policy = Policy.Builder.newInstance()
            .permission(permission)
            .build();
        var errors = MappingErrors.root();

        var uiExpression = mock(UiPolicyExpression.class);
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(constraint)), any())).thenReturn(List.of(uiExpression));

        // act
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(permission), any());
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(uiExpression);
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getPermissionConstraints_merge_constraints() {
        // arrange
        var first = mock(Constraint.class);
        var firstPermission = Permission.Builder.newInstance()
            .constraint(first)
            .build();

        var second = mock(Constraint.class);
        var secondPermission = Permission.Builder.newInstance()
            .constraint(second)
            .build();

        var policy = Policy.Builder.newInstance()
            .permission(firstPermission)
            .permission(secondPermission)
            .build();
        var errors = MappingErrors.root();

        var firstUiExpression = mock(UiPolicyExpression.class);
        var secondUiExpression = mock(UiPolicyExpression.class);
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(first)), any())).thenReturn(List.of(firstUiExpression));
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(second)), any())).thenReturn(List.of(secondUiExpression));

        // act
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(firstPermission), any());
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(secondPermission), any());
        var expected = UiPolicyExpression.and(List.of(firstUiExpression, secondUiExpression));
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);

    }

    @Test
    void test_getPermissionConstraints_merge_constraints2() {
        // arrange
        var first = mock(Constraint.class);
        var second = mock(Constraint.class);
        var permission = Permission.Builder.newInstance()
            .constraints(List.of(first, second))
            .build();

        var policy = Policy.Builder.newInstance()
            .permission(permission)
            .build();

        var errors = MappingErrors.root();

        var firstUiExpression = mock(UiPolicyExpression.class);
        var secondUiExpression = mock(UiPolicyExpression.class);
        when(expressionMapper.buildUiPolicyExpressions(eq(List.of(first, second)), any()))
            .thenReturn(List.of(firstUiExpression, secondUiExpression));

        // act
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(permission), any());
        var expected = UiPolicyExpression.and(List.of(firstUiExpression, secondUiExpression));
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
        assertThat(errors.getErrors()).containsExactly(
            "$.permissions[0].constraints: Multiple constraints were present. Prefer using a conjunction using AND."
        );
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
        var actual = expressionExtractor.getPermissionExpression(policy, errors);

        // assert
        verify(policyValidator).validateOtherPermissionFieldsUnset(same(permission), any());
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(UiPolicyExpression.empty());
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
            "$.permissions[0].constraints: test"
        );
    }
}
