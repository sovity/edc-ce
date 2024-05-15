package de.sovity.edc.ext.wrapper.api.usecase.pages.catalog;

import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogFilterExpression;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
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
