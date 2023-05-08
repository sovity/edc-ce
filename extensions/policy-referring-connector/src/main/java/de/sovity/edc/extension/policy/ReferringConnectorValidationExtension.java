/*
 * Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - adaptation and modifications
 *
 */

package de.sovity.edc.extension.policy;

import de.sovity.edc.extension.policy.functions.ReferringConnectorDutyFunction;
import de.sovity.edc.extension.policy.functions.ReferringConnectorPermissionFunction;
import de.sovity.edc.extension.policy.functions.ReferringConnectorProhibitionFunction;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Prohibition;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

public class ReferringConnectorValidationExtension implements ServiceExtension {

    /**
     * The key for referring connector constraints.
     * Must be used as left operand when declaring constraints.
     *
     * <p>Example:
     *
     * <pre>
     * {
     *     "constraint": {
     *         "leftOperand": "REFERRING_CONNECTOR",
     *         "operator": "EQ",
     *         "rightOperand": "http://example.org"
     *     }
     * }
     * </pre>
     * <p>
     * Constraint:
     * <pre>
     *       {
     *         "edctype": "AtomicConstraint",
     *         "leftExpression": {
     *           "edctype": "dataspaceconnector:literalexpression",
     *           "value": "REFERRING_CONNECTOR"
     *         },
     *         "rightExpression": {
     *           "edctype": "dataspaceconnector:literalexpression",
     *           "value": "http://example.org"
     *         },
     *         "operator": "EQ"
     *       }
     * </pre>
     */
    public static final String REFERRING_CONNECTOR_CONSTRAINT_KEY = "REFERRING_CONNECTOR";
    public static final String BUSINESS_PARTNER_NUMBER_CONSTRAINT_KEY = "BusinessPartnerNumber";

    public ReferringConnectorValidationExtension() {
    }

    public ReferringConnectorValidationExtension(final RuleBindingRegistry ruleBindingRegistry,
                                                 final PolicyEngine policyEngine) {
        this.ruleBindingRegistry = ruleBindingRegistry;
        this.policyEngine = policyEngine;
    }

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private PolicyEngine policyEngine;

    @Override
    public String name() {
        return "ReferringConnector Validation Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        final var monitor = context.getMonitor();

        ruleBindingRegistry.bind("USE", ALL_SCOPES);
        ruleBindingRegistry.bind(REFERRING_CONNECTOR_CONSTRAINT_KEY, ALL_SCOPES);

        registerPolicyFunctions(REFERRING_CONNECTOR_CONSTRAINT_KEY, monitor);
        registerPolicyFunctions(BUSINESS_PARTNER_NUMBER_CONSTRAINT_KEY, monitor);
    }

    private void registerPolicyFunctions(String leftExpressionKey, Monitor monitor) {
        policyEngine.registerFunction(ALL_SCOPES, Duty.class, leftExpressionKey,
                new ReferringConnectorDutyFunction(monitor));
        policyEngine.registerFunction(ALL_SCOPES, Permission.class, leftExpressionKey,
                new ReferringConnectorPermissionFunction(monitor));
        policyEngine.registerFunction(ALL_SCOPES, Prohibition.class, leftExpressionKey,
                new ReferringConnectorProhibitionFunction(monitor));
    }
}