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

package de.sovity.edc.extension.policy;

import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class PolicyEvaluationTimeFunction implements AtomicConstraintFunction<Permission> {
    private final Monitor monitor;

    public PolicyEvaluationTimeFunction(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        try {
            var policyDate = OffsetDateTime.parse((String) rightValue);
            var nowDate = OffsetDateTime.now();
            return switch (operator) {
                case LT -> nowDate.isBefore(policyDate);
                case LEQ -> nowDate.isBefore(policyDate) || nowDate.equals(policyDate);
                case GT -> nowDate.isAfter(policyDate);
                case GEQ -> nowDate.isAfter(policyDate) || nowDate.equals(policyDate);
                case EQ -> nowDate.equals(policyDate);
                case NEQ -> !nowDate.equals(policyDate);
                default -> false;
            };
        } catch (DateTimeParseException e) {
            monitor.severe("Failed to parse right value of constraint to date.");
            return false;
        }
    }
}
