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

package de.sovity.edc.ce.libs.mappers.policy;

import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.Prohibition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PolicyValidatorTest {
    @InjectMocks
    PolicyValidator policyValidator;

    @Test
    void testPolicy_null() {
        // arrange
        var errors = MappingErrors.root();
        var policy = (Policy) null;

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$: Policy is null");
    }

    @Test
    void testPolicy_ok() {
        // arrange
        var errors = MappingErrors.root();
        var policy = Policy.Builder.newInstance()
            .type(PolicyType.SET)
            .permission(mock(Permission.class))
            .build();

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void testPolicy_full() {
        // arrange
        var errors = MappingErrors.root();
        var policy = Policy.Builder.newInstance()
            .prohibition(mock(Prohibition.class))
            .duty(mock(Duty.class))
            .inheritsFrom("inheritsFrom")
            .assigner("assigner")
            .assignee("assignee")
            .target("target")
            .type(PolicyType.OFFER)
            .extensibleProperty("some", "prop")
            .build();

        // act
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        // assert
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
            "$: Warning: Policy has no permissions.",
            "$: Warning: Policy has prohibitions, which are currently unsupported.",
            "$: Warning: Policy has obligations, which are currently unsupported.",
            "$: Warning: Policy has inheritsFrom, which is currently unsupported.",
            "$: Warning: Policy has extensible properties."
        );
    }

    @Test
    void testPermission_null() {
        // arrange
        var errors = MappingErrors.root();
        var permission = (Permission) null;

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$: Warning: Permission is null.");
    }

    @Test
    void testPermission_action_null() {
        // arrange
        var errors = MappingErrors.root();
        var permission = Permission.Builder.newInstance()
            .constraint(mock(Constraint.class))
            .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactly("$.action: Action is null.");
    }

    @Test
    void testPermission_ok() {
        // arrange
        var errors = MappingErrors.root();
        var permission = Permission.Builder.newInstance()
            .constraint(mock(Constraint.class))
            .action(Action.Builder.newInstance().type("USE").build())
            .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void testPermission_full() {
        // arrange
        var errors = MappingErrors.root();
        var action = Action.Builder.newInstance()
            .type("idk")
            .constraint(mock(Constraint.class))
            .includedIn("includedIn")
            .build();
        var permission = Permission.Builder.newInstance()
            .duty(mock(Duty.class))
            .action(action)
            .build();

        // act
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        // assert
        assertThat(errors.getErrors()).containsExactlyInAnyOrder(
            "$: Permission has duties, which is currently unsupported.",
            "$.action: Action has a type that is not '[http://www.w3.org/ns/odrl/2/use, USE, use]', but 'idk'.",
            "$.action: Action has a value for includedIn, which is currently unsupported.",
            "$.action: Action has a constraint, which is currently unsupported."
        );
    }
}
