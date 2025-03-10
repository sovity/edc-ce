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
import de.sovity.edc.ce.api.common.model.UiPolicyExpressionType;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ExpressionMapper {
    private final AtomicConstraintMapper atomicConstraintMapper;

    public List<Constraint> buildConstraints(
        @Nullable List<UiPolicyExpression> expressions
    ) {
        if (expressions == null) {
            return List.of();
        }

        return expressions.stream()
            .map(this::buildConstraint)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    @NotNull
    public List<UiPolicyExpression> buildUiPolicyExpressions(
        @Nullable List<Constraint> constraints,
        @NonNull MappingErrors errors
    ) {
        if (constraints == null) {
            errors.add("Constraints are null.");
            return List.of();
        }

        var expressions = new ArrayList<UiPolicyExpression>();
        for (int i = 0; i < constraints.size(); i++) {
            var constraintErrors = errors.forChildArrayElement(i);
            var constraint = constraints.get(i);

            buildUiPolicyExpression(constraint, constraintErrors).ifPresent(expressions::add);
        }
        return expressions;
    }

    public Optional<Constraint> buildConstraint(UiPolicyExpression expression) {
        if (expression == null || expression.getType() == null) {
            return Optional.empty();
        }

        return switch (expression.getType()) {
            case EMPTY -> Optional.empty();
            case AND -> Optional.of(AndConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build());
            case OR -> Optional.of(OrConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build());
            case XONE -> Optional.of(XoneConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build());
            case CONSTRAINT -> Optional.of(atomicConstraintMapper
                .buildAtomicConstraint(expression.getConstraint()));
        };
    }

    private Optional<UiPolicyExpression> buildUiPolicyExpression(Constraint constraint, MappingErrors errors) {
        if (constraint == null) {
            errors.add("Expression is null.");
            return Optional.empty();
        }

        if (constraint instanceof XoneConstraint xone) {
            return buildMultiUiPolicyExpression(
                UiPolicyExpressionType.XONE,
                xone.getConstraints(),
                errors.forChildObject("constraints")
            );
        } else if (constraint instanceof AndConstraint and) {
            return buildMultiUiPolicyExpression(
                UiPolicyExpressionType.AND,
                and.getConstraints(),
                errors.forChildObject("constraints")
            );
        } else if (constraint instanceof OrConstraint or) {
            return buildMultiUiPolicyExpression(
                UiPolicyExpressionType.OR,
                or.getConstraints(),
                errors.forChildObject("constraints")
            );
        } else if (constraint instanceof AtomicConstraint atomic) {
            return atomicConstraintMapper.buildUiConstraint(atomic, errors)
                .map(this::buildConstraintUiPolicyExpression);
        }

        errors.add("Unknown expression type %s.".formatted(constraint.getClass().getName()));
        return Optional.empty();
    }

    private Optional<UiPolicyExpression> buildMultiUiPolicyExpression(
        UiPolicyExpressionType type,
        List<Constraint> constraints,
        MappingErrors errors
    ) {
        var expressions = buildUiPolicyExpressions(constraints, errors);
        var expression = UiPolicyExpression.builder()
            .type(type)
            .expressions(expressions)
            .build();
        return Optional.of(expression);
    }

    private UiPolicyExpression buildConstraintUiPolicyExpression(UiPolicyConstraint constraint) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(constraint)
            .build();
    }
}
