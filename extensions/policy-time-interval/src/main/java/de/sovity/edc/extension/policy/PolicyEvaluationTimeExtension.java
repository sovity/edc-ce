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

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

public class PolicyEvaluationTimeExtension implements ServiceExtension {

    private static final String KEY_POLICY_EVALUATION_TIME = "POLICY_EVALUATION_TIME";
    private static final String EXTENSION_NAME = "Policy Function: POLICY_EVALUATION_TIME";

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private PolicyEngine policyEngine;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        ruleBindingRegistry.bind("USE", ALL_SCOPES);
        ruleBindingRegistry.bind(KEY_POLICY_EVALUATION_TIME, ALL_SCOPES);
        policyEngine.registerFunction(
                ALL_SCOPES,
                Permission.class,
                KEY_POLICY_EVALUATION_TIME,
                new PolicyEvaluationTimeFunction(monitor));
    }
}
