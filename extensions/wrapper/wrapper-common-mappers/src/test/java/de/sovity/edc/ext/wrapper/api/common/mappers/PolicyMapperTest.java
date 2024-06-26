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

import de.sovity.edc.ext.wrapper.api.common.mappers.policy.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.model.AtomicConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.Expression;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionType;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static de.sovity.edc.ext.wrapper.api.common.model.ExpressionType.ATOMIC_CONSTRAINT;
import static de.sovity.edc.utils.JsonUtils.parseJsonObj;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyMapperTest {
    @InjectMocks
    PolicyMapper policyMapper;

    @Mock
    TypeTransformerRegistry transformerRegistry;

    @Mock
    ConstraintExtractor constraintExtractor;

    @Mock
    AtomicConstraintMapper atomicConstraintMapper;


    @Test
    @SneakyThrows
    void test_buildPolicyDto() {
        try (MockedStatic<MappingErrors> mappingErrors = mockStatic(MappingErrors.class)) {
            // arrange
            var policy = mock(Policy.class);
            var errors = mock(MappingErrors.class);
            var constraints = List.of(mock(UiPolicyConstraint.class));

            when(errors.getErrors()).thenReturn(List.of("error1"));

            mappingErrors.when(MappingErrors::root).thenReturn(errors);
            when(constraintExtractor.getPermissionConstraints(policy, errors)).thenReturn(constraints);
            when(transformerRegistry.transform(policy, JsonObject.class)).thenReturn(Result.success(parseJsonObj("{}")));

            // act
            var actual = policyMapper.buildUiPolicy(policy);

            // assert
            assertThat(actual.getPolicyJsonLd()).isEqualTo("{}");
            assertThat(actual.getConstraints()).isEqualTo(constraints);
            assertThat(actual.getErrors()).isEqualTo(List.of("error1"));
        }
    }

    @Test
    void test_buildPolicy() {
        // arrange
        var constraint = mock(UiPolicyConstraint.class);
        var createRequest = new UiPolicyCreateRequest(List.of(constraint));

        var expected = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraints(eq(List.of(constraint))))
                .thenReturn(List.of(expected));

        // act
        var actual = policyMapper.buildPolicy(createRequest);

        // assert
        assertThat(actual.getType()).isEqualTo(PolicyType.SET);
        assertThat(actual.getPermissions()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getConstraints()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
        assertThat(actual.getPermissions().get(0).getConstraints().get(0)).isSameAs(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AND", "OR", "XOR"})
    void buildGenericPolicy(String constraintTypeString) {
        // arrange
        var expressionType = ExpressionType.valueOf(constraintTypeString);
        var incomingConstraint = mock(AtomicConstraintDto.class);
        var mappedAtomicConstraint = mock(AtomicConstraint.class);
        var atomicConstraint = new Expression(ATOMIC_CONSTRAINT, List.of(), incomingConstraint);
        var atomicConstraints = List.of(atomicConstraint, atomicConstraint);
        var baseConstraintElement = new Expression(expressionType, atomicConstraints, null);

        // act
        when(atomicConstraintMapper
                .buildAtomicConstraint(eq(incomingConstraint)))
                .thenReturn(mappedAtomicConstraint);
        var policy = policyMapper.buildPolicy(List.of(baseConstraintElement));

        // assert
        assertThat(policy.getType()).isEqualTo(PolicyType.SET);
        assertThat(policy.getPermissions()).hasSize(1);
        var permission = policy.getPermissions().get(0);
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getAction().getType()).isEqualTo("USE");

        var constraintObject = permission.getConstraints().get(0);
        assertNotNull(constraintObject);
    }
}
