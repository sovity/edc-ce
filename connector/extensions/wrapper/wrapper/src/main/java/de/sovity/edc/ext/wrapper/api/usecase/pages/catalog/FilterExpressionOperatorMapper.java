/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.usecase.pages.catalog;

import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogFilterExpressionOperator;
import de.sovity.edc.ext.wrapper.utils.MapUtils;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class FilterExpressionOperatorMapper {
    /**
     * @see org.eclipse.edc.connector.defaults.storage.CriterionToPredicateConverterImpl
     */
    private final Map<CatalogFilterExpressionOperator, String> mappings = Map.of(
            CatalogFilterExpressionOperator.EQ, "=",
            CatalogFilterExpressionOperator.LIKE, "like",
            CatalogFilterExpressionOperator.IN, "in"
    );

    private final Map<String, CatalogFilterExpressionOperator> reverseMappings = MapUtils.reverse(mappings);

    public String getCriterionOperator(CatalogFilterExpressionOperator operator) {
        String result = mappings.get(operator);
        return requireNonNull(result, () -> "Unhandled %s: %s".formatted(
                CatalogFilterExpressionOperator.class.getName(), operator));
    }

    public CatalogFilterExpressionOperator getCatalogFilterExpressionOperator(String operator) {
        CatalogFilterExpressionOperator result = reverseMappings.get(operator == null ? null : operator.toLowerCase());
        return requireNonNull(result, () -> "Could not find %s for: %s".formatted(
                CatalogFilterExpressionOperator.class.getName(), operator));
    }
}
