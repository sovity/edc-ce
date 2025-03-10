/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import de.sovity.edc.ce.libs.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ce.libs.mappers.policy.ExpressionMapper;
import de.sovity.edc.ce.libs.mappers.policy.MappingErrors;
import de.sovity.edc.runtime.config.ConfigUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.val;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ConfigUtils configUtils;

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

        val managementApiTransformer = mock(TypeTransformerRegistry.class);
        when(managementApiTransformer.transform(eq(policy), eq(JsonObject.class)))
            .thenReturn(Result.success(Json.createObjectBuilder().add("a", "b").build()));

        when(typeTransformerRegistry.forContext("management-api")).thenReturn(managementApiTransformer);

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
