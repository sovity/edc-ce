/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ce.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
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

        return buildUiPolicyExpression(createRequest.getConstraints());
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
