/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_definitions;


import de.sovity.edc.ce.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ce.api.ui.model.UiCriterionLiteralType;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CriterionLiteralMapper {

    public UiCriterionLiteral buildUiCriterionLiteral(Object value) {
        if (value instanceof Collection) {
            var list = getValueList((Collection<?>) value);
            return UiCriterionLiteral.ofValueList(list);
        }

        return UiCriterionLiteral.ofValue(value.toString());
    }

    public Object getValue(UiCriterionLiteral dto) {
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
