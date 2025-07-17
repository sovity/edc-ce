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

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@RequiredArgsConstructor
@Service
public class PolicyValidator {

    public static final List<String> ALLOWED_ACTION_VALUES = List.of(
        "USE",
        "use",
        "http://www.w3.org/ns/odrl/2/use"
    );

    public void validateOtherPolicyFieldsUnset(Policy policy, MappingErrors errors) {
        if (policy == null) {
            errors.add("Policy is null");
            return;
        }

        if (isEmpty(policy.getPermissions())) {
            errors.add("Warning: Policy has no permissions.");
        }

        if (isNotEmpty(policy.getProhibitions())) {
            errors.add("Warning: Policy has prohibitions, which are currently unsupported.");
        }

        if (isNotEmpty(policy.getObligations())) {
            errors.add("Warning: Policy has obligations, which are currently unsupported.");
        }

        if (StringUtils.isNotBlank(policy.getInheritsFrom())) {
            errors.add("Warning: Policy has inheritsFrom, which is currently unsupported.");
        }

        if (StringUtils.isNotBlank(policy.getAssignee())) {
            errors.add("Warning: Policy has an assignee, which is currently unsupported.");
        }

        if (policy.getExtensibleProperties() != null && !policy.getExtensibleProperties().isEmpty()) {
            errors.add("Warning: Policy has extensible properties.");
        }
    }

    public void validateOtherPermissionFieldsUnset(Permission permission, MappingErrors errors) {
        if (permission == null) {
            errors.add("Warning: Permission is null.");
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

        if (!ALLOWED_ACTION_VALUES.contains(action.getType())) {
            errors.add("Action has a type that is not '%s', but '%s'.".formatted(ALLOWED_ACTION_VALUES, action.getType()));
        }

        if (StringUtils.isNotBlank(action.getIncludedIn())) {
            errors.add("Action has a value for includedIn, which is currently unsupported.");
        }

        if (action.getConstraint() != null) {
            errors.add("Action has a constraint, which is currently unsupported.");
        }
    }
}
