/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.XoneConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ConstraintExtractor {
    private final PolicyValidator policyValidator;
    private final AtomicConstraintMapper atomicConstraintMapper;

    /**
     * Build {@link UiPolicyConstraint}s from an ODRL {@link Policy}.
     * <p>
     * This operation is lossy which is why we document warnings / errors in {@link MappingErrors}.
     *
     * @param policy ODRL policy
     * @param errors mapping errors
     * @return ui policy constraints
     */
    public List<UiPolicyConstraint> getPermissionConstraints(Policy policy, MappingErrors errors) {
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        var permissions = policy.getPermissions();
        if (permissions == null) {
            return List.of();
        }


        List<UiPolicyConstraint> constraints = new ArrayList<>();
        for (int iPermission = 0; iPermission < permissions.size(); iPermission++) {
            var permissionErrors = errors.forChildObject("permissions").forChildArrayElement(iPermission);
            var permission = permissions.get(iPermission);
            constraints.addAll(getPermissionConstraints(permission, permissionErrors));
        }
        return constraints;
    }

    private List<UiPolicyConstraint> getPermissionConstraints(Permission permission, MappingErrors errors) {
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        if (permission == null) {
            return List.of();
        }

        var constraints = permission.getConstraints();
        if (constraints == null) {
            return List.of();
        }

        var constraintsMapped = new ArrayList<UiPolicyConstraint>();
        for (int i = 0; i < constraints.size(); i++) {
            var constraintErrors = errors.forChildObject("constraints").forChildArrayElement(i);
            var constraint = constraints.get(i);

            var constraintMapped = buildConstraint(constraint, constraintErrors);
            constraintMapped.ifPresent(constraintsMapped::add);
        }
        return constraintsMapped;
    }

    private Optional<UiPolicyConstraint> buildConstraint(Constraint constraint, MappingErrors errors) {
        if (constraint == null) {
            errors.add("Constraint is null.");
            return Optional.empty();
        }

        if (constraint instanceof XoneConstraint) {
            errors.add("XoneConstraints are currently unsupported.");
            return Optional.empty();
        }

        if (constraint instanceof AndConstraint) {
            errors.add("AndConstraints are currently unsupported.");
            return Optional.empty();
        }

        if (constraint instanceof OrConstraint) {
            errors.add("OrConstraints are currently unsupported.");
            return Optional.empty();
        }

        if (!(constraint instanceof AtomicConstraint)) {
            errors.add("Unknown constraint type %s.".formatted(constraint.getClass().getName()));
            return Optional.empty();
        }

        return atomicConstraintMapper.buildUiConstraint((AtomicConstraint) constraint, errors);
    }
}
