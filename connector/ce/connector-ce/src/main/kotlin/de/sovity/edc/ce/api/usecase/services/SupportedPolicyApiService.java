/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services;

import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;

import java.util.List;

import static de.sovity.edc.ce.api.utils.FieldAccessUtils.accessField;

@RequiredArgsConstructor
@Service
public class SupportedPolicyApiService {
    private final PolicyEngine policyEngine;

    public List<String> getSupportedFunctions() {
        List<Object> constraintFunctions = accessField(policyEngine, "constraintFunctions");
        return constraintFunctions.stream()
            .map(it -> (String) accessField(it, "key"))
            .distinct()
            .sorted()
            .toList();
    }
}
