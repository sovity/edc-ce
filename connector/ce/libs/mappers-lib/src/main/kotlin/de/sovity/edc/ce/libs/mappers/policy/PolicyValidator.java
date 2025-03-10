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

import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@RequiredArgsConstructor
@Service
public class PolicyValidator {

    public static final String ALLOWED_ACTION = "USE";

    public void validateOtherPolicyFieldsUnset(Policy policy, MappingErrors errors) {
        if (policy == null) {
            errors.add("Policy is null");
            return;
        }

        if (isEmpty(policy.getPermissions())) {
            errors.add("Policy has no permissions.");
        }

        if (isNotEmpty(policy.getProhibitions())) {
            errors.add("Policy has prohibitions, which are currently unsupported.");
        }

        if (isNotEmpty(policy.getObligations())) {
            errors.add("Policy has obligations, which are currently unsupported.");
        }

        if (StringUtils.isNotBlank(policy.getInheritsFrom())) {
            errors.add("Policy has inheritsFrom, which is currently unsupported.");
        }

        if (StringUtils.isNotBlank(policy.getAssigner())) {
            errors.add("Policy has an assigner, which is currently unsupported.");
        }

        if (StringUtils.isNotBlank(policy.getAssignee())) {
            errors.add("Policy has an assignee, which is currently unsupported.");
        }

        if (policy.getExtensibleProperties() != null && !policy.getExtensibleProperties().isEmpty()) {
            errors.add("Policy has extensible properties.");
        }

        if (policy.getType() != PolicyType.SET) {
            errors.add("Policy does not have type SET, but %s, which is currently unsupported.".formatted(policy.getType()));
        }
    }

    public void validateOtherPermissionFieldsUnset(Permission permission, MappingErrors errors) {
        if (permission == null) {
            errors.add("Permission is null.");
            return;
        }

        if (CollectionUtils.isNotEmpty(permission.getDuties())) {
            errors.add("Permission has duties, which is currently unsupported.");
        }

        validateAction(permission.getAction(), errors.forChildObject("action"));
    }

    private void validateAction(Action action, MappingErrors errors) {
        if (action == null) {
            errors.add("Action is null.");
            return;
        }

        if (!ALLOWED_ACTION.equals(action.getType())) {
            errors.add("Action has a type that is not '%s', but '%s'.".formatted(ALLOWED_ACTION, action.getType()));
        }

        if (StringUtils.isNotBlank(action.getIncludedIn())) {
            errors.add("Action has a value for includedIn, which is currently unsupported.");
        }

        if (action.getConstraint() != null) {
            errors.add("Action has a constraint, which is currently unsupported.");
        }
    }
}
