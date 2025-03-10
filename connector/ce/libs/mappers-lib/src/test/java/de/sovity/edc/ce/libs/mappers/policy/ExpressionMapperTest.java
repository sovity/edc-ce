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
package de.sovity.edc.ce.libs.mappers.policy;

import de.sovity.edc.ce.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpressionMapperTest {
    @InjectMocks
    ExpressionMapper expressionMapper;

    @Mock
    AtomicConstraintMapper atomicConstraintMapper;

    @Test
    void buildUiConstraint_errorPropagation() {
        // arrange
        var errors = MappingErrors.root();
        var atomicConstraint = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(atomicConstraint), any()))
            .thenAnswer(i -> {
                i.getArgument(1, MappingErrors.class).add("test");
                return Optional.empty();
            });

        var constraints = List.<Constraint>of(atomicConstraint);

        // act
        var actual = expressionMapper.buildUiPolicyExpressions(constraints, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$[0]: test");
    }

    @Test
    void buildUiConstraint_simpleAtomicConstraint() {
        // arrange
        var atomicConstraint = mock(AtomicConstraint.class);
        var uiConstraint = mock(UiPolicyConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(atomicConstraint), any()))
            .thenReturn(Optional.of(uiConstraint));

        var constraints = List.<Constraint>of(atomicConstraint);

        // act
        var actual = expressionMapper.buildUiPolicyExpressions(constraints, MappingErrors.root());

        // assert
        assertThat(actual).containsExactly(
            UiPolicyExpression.constraint(uiConstraint)
        );
    }

    @Test
    void buildUiConstraint_andConstraint() {
        // arrange
        var atomicConstraint = mock(AtomicConstraint.class);
        var uiConstraint = mock(UiPolicyConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(atomicConstraint), any()))
            .thenReturn(Optional.of(uiConstraint));

        var constraints = List.<Constraint>of(
            AndConstraint.Builder.newInstance()
                .constraint(atomicConstraint)
                .build()
        );

        // act
        var actual = expressionMapper.buildUiPolicyExpressions(constraints, MappingErrors.root());

        // assert
        assertThat(actual).containsExactly(
            UiPolicyExpression.and(List.of(
                UiPolicyExpression.constraint(uiConstraint)
            ))
        );
    }

    @Test
    void buildUiConstraint_orConstraint() {
        // arrange
        var atomicConstraint = mock(AtomicConstraint.class);
        var uiConstraint = mock(UiPolicyConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(atomicConstraint), any()))
            .thenReturn(Optional.of(uiConstraint));

        var constraints = List.<Constraint>of(
            OrConstraint.Builder.newInstance()
                .constraint(atomicConstraint)
                .build()
        );

        // act
        var actual = expressionMapper.buildUiPolicyExpressions(constraints, MappingErrors.root());

        // assert
        assertThat(actual).containsExactly(
            UiPolicyExpression.or(List.of(
                UiPolicyExpression.constraint(uiConstraint)
            ))
        );
    }

    @Test
    void buildUiConstraint_xoneConstraint() {
        // arrange
        var atomicConstraint = mock(AtomicConstraint.class);
        var uiConstraint = mock(UiPolicyConstraint.class);
        when(atomicConstraintMapper.buildUiConstraint(same(atomicConstraint), any()))
            .thenReturn(Optional.of(uiConstraint));

        var constraints = List.<Constraint>of(
            XoneConstraint.Builder.newInstance()
                .constraint(atomicConstraint)
                .build()
        );

        // act
        var actual = expressionMapper.buildUiPolicyExpressions(constraints, MappingErrors.root());

        // assert
        assertThat(actual).containsExactly(
            UiPolicyExpression.xone(List.of(
                UiPolicyExpression.constraint(uiConstraint)
            ))
        );
    }

    @Test
    void buildConstraint_atomicConstraint() {
        // arrange
        var uiConstraint = mock(UiPolicyConstraint.class);
        var expression = UiPolicyExpression.constraint(uiConstraint);

        var atomicConstraint = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraint(uiConstraint))
            .thenReturn(atomicConstraint);

        // act
        var actual = expressionMapper.buildConstraints(List.of(expression));

        // assert
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(atomicConstraint);
    }

    @Test
    void buildConstraint_andConstraint() {
        // arrange
        var uiConstraint = mock(UiPolicyConstraint.class);
        var expression = UiPolicyExpression.and(List.of(
            UiPolicyExpression.constraint(uiConstraint)
        ));

        var atomicConstraint = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraint(uiConstraint))
            .thenReturn(atomicConstraint);

        // act
        var actual = expressionMapper.buildConstraints(List.of(expression));

        // assert
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isInstanceOf(AndConstraint.class);

        var constraints = ((AndConstraint) actual.get(0)).getConstraints();
        assertThat(constraints).hasSize(1);
        assertThat(constraints.get(0)).isEqualTo(atomicConstraint);
    }

    @Test
    void buildConstraint_orConstraint() {
        // arrange
        var uiConstraint = mock(UiPolicyConstraint.class);
        var expression = UiPolicyExpression.or(List.of(
            UiPolicyExpression.constraint(uiConstraint)
        ));

        var atomicConstraint = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraint(uiConstraint))
            .thenReturn(atomicConstraint);

        // act
        var actual = expressionMapper.buildConstraints(List.of(expression));

        // assert
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isInstanceOf(OrConstraint.class);

        var constraints = ((OrConstraint) actual.get(0)).getConstraints();
        assertThat(constraints).hasSize(1);
        assertThat(constraints.get(0)).isEqualTo(atomicConstraint);
    }

    @Test
    void buildConstraint_xoneConstraint() {
        // arrange
        var uiConstraint = mock(UiPolicyConstraint.class);
        var expression = UiPolicyExpression.xone(List.of(
            UiPolicyExpression.constraint(uiConstraint)
        ));

        var atomicConstraint = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraint(uiConstraint))
            .thenReturn(atomicConstraint);

        // act
        var actual = expressionMapper.buildConstraints(List.of(expression));

        // assert
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isInstanceOf(XoneConstraint.class);

        var constraints = ((XoneConstraint) actual.get(0)).getConstraints();
        assertThat(constraints).hasSize(1);
        assertThat(constraints.get(0)).isEqualTo(atomicConstraint);
    }
}
