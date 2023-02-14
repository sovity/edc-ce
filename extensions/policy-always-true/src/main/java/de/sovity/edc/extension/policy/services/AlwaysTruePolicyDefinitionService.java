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

import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;

import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE;
import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE;
import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.POLICY_DEFINITION_ID;

/**
 * Creates policy definition &quot;always-true&quot;.
 */
public class AlwaysTruePolicyDefinitionService {
    private final PolicyDefinitionService policyDefinitionService;

    public AlwaysTruePolicyDefinitionService(PolicyDefinitionService policyDefinitionService) {
        this.policyDefinitionService = policyDefinitionService;
    }

    /**
     * Checks if policy definition &quot;always-true&quot; exists
     *
     * @return if exists
     */
    public boolean exists() {
        return policyDefinitionService.findById(POLICY_DEFINITION_ID) != null;
    }

    /**
     * Creates policy definition &quot;always-true&quot;.
     */
    public void create() {
        var alwaysTrueConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(EXPRESSION_LEFT_VALUE))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression(EXPRESSION_RIGHT_VALUE))
                .build();
        var alwaysTruePermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .constraint(alwaysTrueConstraint)
                .build();
        var policy = Policy.Builder.newInstance()
                .permission(alwaysTruePermission)
                .build();
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .id(POLICY_DEFINITION_ID)
                .policy(policy)
                .build();
        policyDefinitionService.create(policyDefinition);
    }
}
