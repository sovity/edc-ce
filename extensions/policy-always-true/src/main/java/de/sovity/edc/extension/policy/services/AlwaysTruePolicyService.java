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

package de.sovity.edc.extension.policy.services;

import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

/**
 * Creates policy &quot;Always True&quot;.
 * <p>
 * To be exact, it resolves to true iff constraint is {@link AlwaysTruePolicyConstants#EXPRESSION_LEFT_VALUE}
 * &quot;EQ&quot; {@link AlwaysTruePolicyConstants#EXPRESSION_RIGHT_VALUE}.
 */
public class AlwaysTruePolicyService {
    private final RuleBindingRegistry ruleBindingRegistry;
    private final PolicyEngine policyEngine;

    public AlwaysTruePolicyService(RuleBindingRegistry ruleBindingRegistry, PolicyEngine policyEngine) {
        this.ruleBindingRegistry = ruleBindingRegistry;
        this.policyEngine = policyEngine;
    }

    public void registerPolicy() {
        ruleBindingRegistry.bind("USE", ALL_SCOPES);
        ruleBindingRegistry.bind(AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE, ALL_SCOPES);
        policyEngine.registerFunction(
                ALL_SCOPES,
                Permission.class,
                AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE,
                (operator, rightValue, rule, context1) -> operator.equals(Operator.EQ) &&
                        rightValue.toString().equals(AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE)
        );
    }

}
