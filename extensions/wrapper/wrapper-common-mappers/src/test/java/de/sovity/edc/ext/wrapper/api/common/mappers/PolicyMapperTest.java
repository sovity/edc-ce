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

package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyMapperTest {
    @InjectMocks
    PolicyMapper policyMapper;
    @Mock
    ExpressionExtractor expressionExtractor;
    @Mock
    ExpressionMapper expressionMapper;
    @Mock
    TypeTransformerRegistry typeTransformerRegistry;

    @Test
    void buildUiPolicy() {
        // arrange
        var policy = mock(Policy.class);
        var expression = mock(UiPolicyExpression.class);

        when(expressionExtractor.getPermissionExpressions(eq(policy), any())).thenAnswer(i -> {
            var errors = i.getArgument(1, MappingErrors.class);
            errors.add("test");
            return List.of(expression);
        });

        when(typeTransformerRegistry.transform(eq(policy), eq(JsonObject.class)))
            .thenReturn(Result.success(Json.createObjectBuilder().add("a", "b").build()));

        // act
        var actual = policyMapper.buildUiPolicy(policy);

        // assert
        assertThat(actual.getExpressions()).containsExactly(expression);
        assertThat(actual.getErrors()).containsExactly("$: test");
        assertThat(actual.getPolicyJsonLd()).isEqualTo("{\"a\":\"b\"}");
    }

    @Test
    void buildPolicy() {
        // arrange
        var uiExpression = mock(UiPolicyExpression.class);
        var constraint = mock(Constraint.class);
        when(expressionMapper.buildConstraints(eq(List.of(uiExpression))))
            .thenReturn(List.of(constraint));

        var createRequest = new UiPolicyCreateRequest(List.of(uiExpression));

        // act
        var actual = policyMapper.buildPolicy(createRequest);

        // assert
        assertThat(actual.getType()).isEqualTo(PolicyType.SET);
        assertThat(actual.getPermissions()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getConstraints()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
        assertThat(actual.getPermissions().get(0).getConstraints()).containsExactly(constraint);
    }
}
