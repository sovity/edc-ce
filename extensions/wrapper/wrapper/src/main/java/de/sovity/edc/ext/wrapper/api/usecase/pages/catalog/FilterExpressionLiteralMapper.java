package de.sovity.edc.ext.wrapper.api.usecase.pages.catalog;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralType;
import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogFilterExpressionLiteral;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class FilterExpressionLiteralMapper {
    public UiCriterionLiteral buildUiCriterionLiteral(Object value) {
        if (value instanceof Collection) {
            var list = getValueList((Collection<?>) value);
            return UiCriterionLiteral.ofValueList(list);
        }

        return UiCriterionLiteral.ofValue(value.toString());
    }

    public Object getValue(CatalogFilterExpressionLiteral dto) {
        return switch (dto.getType()) {
            case VALUE -> dto.getValue();
            case VALUE_LIST -> dto.getValueList();
            default -> throw new IllegalStateException("Unhandled %s: %s".formatted(
                    UiCriterionLiteralType.class.getName(),
                    dto.getType()
            ));
        };
    }

    private List<String> getValueList(Collection<?> valueList) {
        return valueList.stream().map(it -> it == null ? null : it.toString()).toList();
    }
}
