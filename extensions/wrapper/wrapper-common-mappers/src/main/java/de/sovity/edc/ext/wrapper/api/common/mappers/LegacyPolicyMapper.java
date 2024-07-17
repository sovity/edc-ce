/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LegacyPolicyMapper {
    /**
     * Builds a {@link UiPolicyExpression} from the legacy {@link UiPolicyCreateRequest}.
     *
     * @param createRequest {@link UiPolicyCreateRequest}
     * @return {@link UiPolicyExpression}
     */
    @Deprecated
    public UiPolicyExpression buildUiPolicyExpression(UiPolicyCreateRequest createRequest) {
        if (createRequest == null) {
            return UiPolicyExpression.empty();
        }

        return buildUiPolicyExpression(createRequest.getExpressions());
    }

    private UiPolicyExpression buildUiPolicyExpression(List<UiPolicyConstraint> expressions) {
        UiPolicyExpression expression;
        if (expressions == null || expressions.isEmpty()) {
            expression = UiPolicyExpression.empty();
        } else if (expressions.size() == 1) {
            expression = UiPolicyExpression.constraint(expressions.get(0));
        } else {
            expression = UiPolicyExpression.and(
                expressions.stream().map(UiPolicyExpression::constraint).toList()
            );
        }
        return expression;
    }
}
