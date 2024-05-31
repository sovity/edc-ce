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


import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionOperator;
import de.sovity.edc.ext.wrapper.utils.MapUtils;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class CriterionOperatorMapper {
    /**
     * @see org.eclipse.edc.connector.defaults.storage.CriterionToPredicateConverterImpl
     */
    private final Map<UiCriterionOperator, String> mappings = Map.of(
            UiCriterionOperator.EQ, "=",
            UiCriterionOperator.LIKE, "like",
            UiCriterionOperator.IN, "in"
    );

    private final Map<String, UiCriterionOperator> reverseMappings = MapUtils.reverse(mappings);

    public String getCriterionOperator(UiCriterionOperator operator) {
        String result = mappings.get(operator);
        return requireNonNull(result, () -> "Unhandled %s: %s".formatted(
                UiCriterionOperator.class.getName(), operator));
    }

    public UiCriterionOperator getUiCriterionOperator(String operator) {
        UiCriterionOperator result = reverseMappings.get(operator == null ? null : operator.toLowerCase());
        return requireNonNull(result, () -> "Could not find %s for: %s".formatted(
                UiCriterionOperator.class.getName(), operator));
    }
}
