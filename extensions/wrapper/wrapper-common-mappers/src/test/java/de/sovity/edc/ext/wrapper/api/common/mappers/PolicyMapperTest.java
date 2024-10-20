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
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import de.sovity.edc.utils.config.CeConfigProps;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyMapperTest {
    @InjectMocks
    private PolicyMapper policyMapper;
    @Mock
    private ExpressionExtractor expressionExtractor;
    @Mock
    private ExpressionMapper expressionMapper;
    @Mock
    private TypeTransformerRegistry typeTransformerRegistry;
    @Mock
    private Config config;

    @Test
    void buildUiPolicy() {
        // arrange
        var policy = mock(Policy.class);
        var expression = mock(UiPolicyExpression.class);

        when(expressionExtractor.getPermissionExpression(eq(policy), any())).thenAnswer(i -> {
            var errors = i.getArgument(1, MappingErrors.class);
            errors.add("test");
            return expression;
        });

        when(typeTransformerRegistry.transform(eq(policy), eq(JsonObject.class)))
            .thenReturn(Result.success(Json.createObjectBuilder().add("a", "b").build()));

        // act
        var actual = policyMapper.buildUiPolicy(policy);

        // assert
        assertThat(actual.getExpression()).isEqualTo(expression);
        assertThat(actual.getErrors()).containsExactly("$: test");
        assertThat(actual.getPolicyJsonLd()).isEqualTo("{\"a\":\"b\"}");
    }

    @Test
    void buildPolicy_constraintExtracted() {
        // arrange
        var uiExpression = mock(UiPolicyExpression.class);
        var constraint = mock(Constraint.class);
        when(expressionMapper.buildConstraint(uiExpression))
            .thenReturn(Optional.of(constraint));

        // act
        var actual = policyMapper.buildPolicy(uiExpression);

        // assert
        assertThat(actual.getType()).isEqualTo(PolicyType.SET);
        assertThat(actual.getPermissions()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
        assertThat(actual.getPermissions().get(0).getConstraints()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getConstraints()).containsExactly(constraint);
    }

    @Test
    void buildPolicy_noConstraint() {
        // arrange
        when(config.getEntries()).thenReturn(
            Map.of(CeConfigProps.EDC_PARTICIPANT_ID.getProperty(), "foo")
        );

        var uiExpression = mock(UiPolicyExpression.class);
        when(expressionMapper.buildConstraint(uiExpression))
            .thenReturn(Optional.empty());

        // act
        var actual = policyMapper.buildPolicy(uiExpression);

        // assert
        assertThat(actual.getType()).isEqualTo(PolicyType.SET);
        assertThat(actual.getPermissions()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getConstraints()).isEmpty();
        assertThat(actual.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
    }
}
