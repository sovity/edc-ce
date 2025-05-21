/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog;

import de.sovity.edc.ce.api.usecase.model.CatalogFilterExpression;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Deprecated
public class FilterExpressionMapper {
    private final FilterExpressionOperatorMapper filterExpressionOperatorMapper;
    private final FilterExpressionLiteralMapper filterExpressionLiteralMapper;

    public List<Criterion> buildCriteria(@NonNull List<CatalogFilterExpression> filterExpressions) {
        return filterExpressions.stream().filter(Objects::nonNull).map(this::buildCriterion).toList();
    }

    public Criterion buildCriterion(@NonNull CatalogFilterExpression filterExpression) {
        var operandLeft = filterExpression.getOperandLeft();
        var operator = filterExpressionOperatorMapper.getCriterionOperator(filterExpression.getOperator());
        var operandRight = filterExpressionLiteralMapper.getValue(filterExpression.getOperandRight());

        return new Criterion(operandLeft, operator, operandRight);
    }
}
