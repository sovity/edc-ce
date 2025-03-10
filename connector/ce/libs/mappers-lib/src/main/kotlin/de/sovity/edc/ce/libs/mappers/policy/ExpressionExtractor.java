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
package de.sovity.edc.ce.libs.mappers.policy;

import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExpressionExtractor {
    private final PolicyValidator policyValidator;
    private final ExpressionMapper expressionMapper;

    /**
     * Build {@link UiPolicyExpression} from an ODRL {@link Policy}.
     * <p>
     * This operation is lossy which is why we document warnings / errors in {@link MappingErrors}.
     *
     * @param policy ODRL policy
     * @param errors mapping errors
     * @return ui policy expression
     */
    public UiPolicyExpression getPermissionExpression(Policy policy, MappingErrors errors) {
        var expressions = getPermissionExpressions(policy, errors);
        if (expressions.isEmpty()) {
            return UiPolicyExpression.empty();
        } else if (expressions.size() == 1) {
            return expressions.get(0);
        } else {
            return UiPolicyExpression.and(expressions);
        }
    }

    /**
     * Build {@link UiPolicyExpression}s from an ODRL {@link Policy}.
     * <p>
     * This operation is lossy which is why we document warnings / errors in {@link MappingErrors}.
     *
     * @param policy ODRL policy
     * @param errors mapping errors
     * @return ui policy expressions
     */
    private List<UiPolicyExpression> getPermissionExpressions(Policy policy, MappingErrors errors) {
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        var permissions = policy.getPermissions();
        if (permissions == null) {
            return List.of();
        }

        if (permissions.size() > 1) {
            errors.add("Multiple permissions were present. Prefer using a conjunction using AND.");
        }

        List<UiPolicyExpression> expressions = new ArrayList<>();
        for (int iPermission = 0; iPermission < permissions.size(); iPermission++) {
            var permissionErrors = errors.forChildObject("permissions").forChildArrayElement(iPermission);
            var permission = permissions.get(iPermission);
            expressions.addAll(getPermissionExpressions(permission, permissionErrors));
        }
        return expressions;
    }

    private List<UiPolicyExpression> getPermissionExpressions(Permission permission, MappingErrors errors) {
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        if (permission == null) {
            return List.of();
        }

        var constraints = permission.getConstraints();
        if (constraints != null && constraints.size() > 1) {
            errors.forChildObject("constraints")
                .add("Multiple constraints were present. Prefer using a conjunction using AND.");
        }

        return expressionMapper.buildUiPolicyExpressions(
            constraints,
            errors.forChildObject("constraints")
        );
    }
}
