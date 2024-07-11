/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.policy;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpressionType;
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

    private Constraint buildConstraint(UiPolicyExpression expression) {
        return switch (expression.getExpressionType()) {
            case AND -> AndConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build();
            case OR -> OrConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build();
            case XOR -> XoneConstraint.Builder.newInstance()
                .constraints(buildConstraints(expression.getExpressions()))
                .build();
            case CONSTRAINT -> atomicConstraintMapper.buildAtomicConstraint(expression.getConstraint());
        };
    }

    private Optional<UiPolicyExpression> buildUiPolicyExpression(Constraint constraint, MappingErrors errors) {
        if (constraint == null) {
            errors.add("Expression is null.");
            return Optional.empty();
        }

        if (constraint instanceof XoneConstraint xone) {
            return buildMultiUiPolicyExpression(
                UiPolicyExpressionType.XOR,
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
            .expressionType(type)
            .expressions(expressions)
            .build();
        return Optional.of(expression);
    }

    private UiPolicyExpression buildConstraintUiPolicyExpression(UiPolicyConstraint constraint) {
        return UiPolicyExpression.builder()
            .expressionType(UiPolicyExpressionType.CONSTRAINT)
            .constraint(constraint)
            .build();
    }
}
