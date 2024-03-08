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
import org.eclipse.edc.connector.contract.spi.offer.ContractDefinitionResolver;
import org.eclipse.edc.connector.contract.spi.validation.ContractValidationService;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Prohibition;
import org.eclipse.edc.policy.model.Rule;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

public class ReferringConnectorValidationExtension implements ServiceExtension {

    /**
     * The key for referring connector constraints.
     * Must be used as left operand when declaring constraints.
     * rightOperand can be a string-URL or a comma separated list of string-URLs.
     * Also supports the IN Operator with a list of string-URLs as right operand.
     *
     * <p>Example:
     *
     * <pre>
     * {
     *     "constraint": {
     *         "leftOperand": "REFERRING_CONNECTOR",
     *         "operator": "EQ",
     *         "rightOperand": "http://example.org,http://example.org"
     *     }
     * }
     * </pre>
     *
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

    public ReferringConnectorValidationExtension() {}

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
        return "Policy Function: REFERRING_CONNECTOR";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        ruleBindingRegistry.bind(REFERRING_CONNECTOR_CONSTRAINT_KEY, ContractValidationService.NEGOTIATION_SCOPE);
        ruleBindingRegistry.bind(REFERRING_CONNECTOR_CONSTRAINT_KEY, ContractDefinitionResolver.CATALOGING_SCOPE);

        var monitor = context.getMonitor();
        registerPolicyFunction(Duty.class, new ReferringConnectorDutyFunction(monitor));
        registerPolicyFunction(Permission.class, new ReferringConnectorPermissionFunction(monitor));
        registerPolicyFunction(Prohibition.class, new ReferringConnectorProhibitionFunction(monitor));
    }

    private <R extends Rule> void registerPolicyFunction(Class<R> type, AtomicConstraintFunction<R> function) {
        policyEngine.registerFunction(ALL_SCOPES, type, REFERRING_CONNECTOR_CONSTRAINT_KEY, function);
    }
}
