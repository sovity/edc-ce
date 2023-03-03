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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeIntervalFunction implements AtomicConstraintFunction<Permission> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final Monitor monitor;
    
    public TimeIntervalFunction(Monitor monitor) {
        this.monitor = monitor;
    }
    
    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        try {
            var policyDate = DATE_FORMAT.parse((String) rightValue);
            var nowDate = new Date();
            return switch (operator) {
                case LT -> nowDate.before(policyDate);
                case LEQ -> nowDate.before(policyDate) || nowDate.equals(policyDate);
                case GT -> nowDate.after(policyDate);
                case GEQ -> nowDate.after(policyDate) || nowDate.equals(policyDate);
                case EQ -> nowDate.equals(policyDate);
                default -> false;
            };
        } catch (ParseException e) {
            monitor.severe("Failed to parse right value of constraint to date.");
            return false;
        }
    }
    
}