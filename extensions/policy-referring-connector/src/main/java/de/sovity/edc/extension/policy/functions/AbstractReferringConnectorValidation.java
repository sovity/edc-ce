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

package de.sovity.edc.extension.policy.functions;

import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class for referringConnector validation. This class may be inherited from the EDC
 * policy enforcing functions for duties, permissions and prohibitions.
 */
public abstract class AbstractReferringConnectorValidation {
    private static final String FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_STRING =
            "Failing evaluation because of invalid referring connector constraint. For operator 'EQ' right value must be of type 'String'. Unsupported type: '%s'";
    private static final String FAIL_EVALUATION_BECAUSE_UNSUPPORTED_OPERATOR =
            "Failing evaluation because of invalid referring connector constraint. Unsupported operator: '%s'";

    private final Monitor monitor;

    protected AbstractReferringConnectorValidation(Monitor monitor) {
        this.monitor = Objects.requireNonNull(monitor);
    }

    private static final String REFERRING_CONNECTOR_CLAIM = "referringConnector";

    /**
     * Evaluation function.
     *
     * @param operator operator of the constraint
     * @param rightValue right value fo the constraint, that contains a referring connector
     * @param policyContext context of the policy with claims
     * @return true if claims are from the constrained referring connector
     */
    protected boolean evaluate(final Operator operator, final Object rightValue, final PolicyContext policyContext) {
        if (policyContext.hasProblems() && !policyContext.getProblems().isEmpty()) {
            var problems = String.join(", ", policyContext.getProblems());
            var message =
                    String.format(
                            "ReferringConnectorValidation: Rejecting PolicyContext with problems. Problems: %s",
                            problems);
            monitor.debug(message);
            return false;
        }

        final var claims = policyContext.getContextData(ParticipantAgent.class).getClaims();

        if (!claims.containsKey(REFERRING_CONNECTOR_CLAIM)) {
            return false;
        }

        Object referringConnectorClaimObject = claims.get(REFERRING_CONNECTOR_CLAIM);
        String referringConnectorClaim = null;

        if (referringConnectorClaimObject instanceof String string) {
            referringConnectorClaim = string;
        }

        if (referringConnectorClaim == null || referringConnectorClaim.isEmpty()) {
            return false;
        }

        if (operator == Operator.EQ || operator == Operator.IN) {
            return isReferringConnector(referringConnectorClaim, rightValue, policyContext, operator);
        } else {
            final var message = String.format(FAIL_EVALUATION_BECAUSE_UNSUPPORTED_OPERATOR, operator);
            monitor.warning(message);
            policyContext.reportProblem(message);
            return false;
        }
    }

    /**
     * Validates if value set in policy and if value interpretable.
     *
     * @param referringConnectorClaim of the participant
     * @param referringConnector object of rightValue of constraint
     * @return true if object is string and successfully evaluated against the claim
     */
    private boolean isReferringConnector(
            String referringConnectorClaim, Object referringConnector, PolicyContext policyContext, Operator operator) {
        //no right value set in policy
        if (referringConnector == null) {
            final var message = String.format(FAIL_EVALUATION_BECAUSE_RIGHT_VALUE_NOT_STRING, "null");
            monitor.warning(message);
            policyContext.reportProblem(message);
            return false;
        }

        //evaluate
        return isAllowedReferringConnector(referringConnectorClaim, referringConnector, operator);
    }

    /**
     * Main evaluation, evaluates if claim is allowed referringConnector.
     *
     * @param referringConnectorClaim of the participant
     * @param referringConnector object of rightValue of constraint
     * @return true if claim equals the referringConnector
     */
    private static boolean isAllowedReferringConnector(
            String referringConnectorClaim, Object referringConnector, Operator operator) {
        if (operator == Operator.IN) {
            var referringConnectorList = (List<?>) referringConnector;
            return referringConnectorList.contains(referringConnectorClaim);
        } else if (operator == Operator.EQ) {
            //support comma separated lists here as well
            if (referringConnector instanceof String referringConnectorString) {
                return Arrays.asList(referringConnectorString.split(",")).contains(referringConnectorClaim);
            }
        }
        return false;
    }
}
