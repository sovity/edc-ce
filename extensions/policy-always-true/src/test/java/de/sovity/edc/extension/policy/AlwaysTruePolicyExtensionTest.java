/*
 *  Copyright (c) 2023 sovity GmbH
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

import org.eclipse.edc.connector.contract.spi.offer.ContractDefinitionService;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.POLICY_DEFINITION_ID;
import static java.util.Objects.requireNonNull;

@ApiTest
@ExtendWith(EdcExtension.class)
class AlwaysTruePolicyExtensionTest {

    @Test
    void alwaysTruePolicyDef(PolicyEngine policyEngine, PolicyDefinitionService policyDefinitionService) {
        // arrange
        var alwaysTrue = alwaysTruePolicy(policyDefinitionService);

        // act
        var result = policyEngine.evaluate(
                ContractDefinitionService.CATALOGING_SCOPE,
                alwaysTrue.getPolicy(),
                participantAgent()
        );

        // assert
        Assertions.assertTrue(result.succeeded(), "Always True Policy wasn't true.");
    }

    @NotNull
    private PolicyDefinition alwaysTruePolicy(PolicyDefinitionService policyDefinitionService) {
        var alwaysTrue = policyDefinitionService.findById(POLICY_DEFINITION_ID);
        requireNonNull(alwaysTrue, "Policy Definition does not exist: " + POLICY_DEFINITION_ID);
        return alwaysTrue;
    }

    @NotNull
    private ParticipantAgent participantAgent() {
        return new ParticipantAgent(Map.of(), Map.of());
    }
}