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

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.model.AtomicConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.LiteralExpression;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AtomicConstraintMapper {
    private final LiteralMapper literalMapper;
    private final OperatorMapper operatorMapper;

    /**
     * Create ODRL {@link AtomicConstraint}s from {@link UiPolicyConstraint}s
     * <p>
     * This operation is lossless.
     *
     * @param constraints ui constraints
     * @return ODRL constraints
     */
    public List<AtomicConstraint> buildAtomicConstraints(List<UiPolicyConstraint> constraints) {
        if (constraints == null) {
            return List.of();
        }

        // unlike this method buildAtomicConstraintS (plural),
        // we are calling a method buildAtomicConstraint here
        // which works with a single constraint

        return constraints.stream()
            .map(this::buildAtomicConstraint)
            .toList();
    }

    /*
     * UiPolicyConstraint is
     * private String left;
     * private OperatorDto operator;
     * private UiPolicyLiteral right;
     *
     * AtomicConstraint is
     * private Expression leftExpression;
     * private Expression rightExpression;
     * private Operator operator = Operator.EQ;
     *
     * but we use LiteralExpression instead of Expression so:
     *
     * AtomicConstraint is
     * private LiteralExpression leftExpression;
     * private LiteralExpression rightExpression;
     * private Operator operator = Operator.EQ;
     *
     * LiteralExpression is a simple class with a single field value
     * and Expression has an interface Visitor with a single method visitLiteralExpression
     *
     * Visitor Pattern is a way of separating an algorithm from an object structure on which it operates.
     *
     * OperatorMapper is used to map the operator
     *  getOperatorDto(String) -> OperatorDto.valueOf(operator.toUpperCase());
     *  getOperatorDto(Operator) -> OperatorDto.valueOf(operator.name());
     *  getOperator(OperatorDto) -> Operator.valueOf(operatorDto.name());
     *
     * OperatorDto is just a simple enum of
     * EQ, NEQ, GT, GTE, LT, LTE, IN, HAS_PART, IS_A, IS_ALL_OF, IS_ANY_OF, IS_NONE_OF
     */
    private AtomicConstraint buildAtomicConstraint(UiPolicyConstraint constraint) {
        var left = constraint.getLeft();
        var operator = operatorMapper.getOperator(constraint.getOperator());
        var right = literalMapper.getUiLiteralValue(constraint.getRight());

        // basic constraint:
        // String "left":"POLICY_EVALUATION_TIME",
        // Operator "operator":"GEQ",
        // the original "right" can be a string or a list of strings or a json object
        // "right":{"type":"STRING","value":"2024-06-15T22:00:00.000Z"}

        return AtomicConstraint.Builder.newInstance()
            // leftExpression is a LiteralExpression of a string
            .leftExpression(new LiteralExpression(left))
            .operator(operator)
            .rightExpression(new LiteralExpression(right))
            .build();
    }

    /**
     * Create {@link UiPolicyConstraint} from ODRL {@link AtomicConstraint}
     * <p>
     * This operation is lossy.
     *
     * @param atomicConstraint atomic contraints
     * @param errors           errors
     * @return ui policy constraint
     */
    public Optional<UiPolicyConstraint> buildUiConstraint(
        @NonNull AtomicConstraint atomicConstraint,
        MappingErrors errors
    ) {
        var leftValue = literalMapper.getExpressionString(atomicConstraint.getLeftExpression(),
            errors.forChildObject("leftExpression"));

        var operator = getOperator(atomicConstraint, errors);

        var rightValue = literalMapper.getExpressionValue(atomicConstraint.getRightExpression(),
            errors.forChildObject("rightExpression"));

        if (leftValue.isEmpty() || rightValue.isEmpty() || operator.isEmpty()) {
            return Optional.empty();
        }

        UiPolicyConstraint result = UiPolicyConstraint.builder()
            .left(leftValue.get())
            .operator(operator.get())
            .right(rightValue.get())
            .build();

        return Optional.of(result);
    }

    private Optional<OperatorDto> getOperator(AtomicConstraint atomicConstraint, MappingErrors errors) {
        var operator = atomicConstraint.getOperator();

        if (operator == null) {
            errors.forChildObject("operator").add("Operator is null.");
            return Optional.empty();
        }

        return Optional.of(operatorMapper.getOperatorDto(operator));
    }

    public AtomicConstraint buildAtomicConstraint(AtomicConstraintDto atomicConstraint) {

        System.err.println("AtomicConstraintMapper.buildAtomicConstraint: " + atomicConstraint);

        var left = atomicConstraint.getLeftExpression();
        var operator = operatorMapper.getOperator(atomicConstraint.getOperator());
        var right = atomicConstraint.getRightExpression();

        System.out.println("AtomicConstraintMapper.buildAtomicConstraint: " + left + " " + operator + " " + right);

        return AtomicConstraint.Builder.newInstance()
            .leftExpression(new LiteralExpression(left))
            .operator(operator)
            .rightExpression(new LiteralExpression(right))
            .build();
    }

//    public AtomicConstraint buildAtomicConstraint(AtomicConstraintDto atomicConstraint) {
//
//                var left = atomicConstraint.getLeftExpression();
//                var operator = operatorMapper.getOperator(atomicConstraint.getOperator());
//                var right = atomicConstraint.getRightExpression();
//
//                System.out.println("AtomicConstraintMapper.buildAtomicConstraint: " + left + " " + operator + " " + right);
//
//                return AtomicConstraint.Builder.newInstance()
//                    .leftExpression(new LiteralExpression(left))
//                    .operator(operator)
//                    .rightExpression(new LiteralExpression(right))
//                    .build();
//    }

}
