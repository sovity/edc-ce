/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;


import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralType;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
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
