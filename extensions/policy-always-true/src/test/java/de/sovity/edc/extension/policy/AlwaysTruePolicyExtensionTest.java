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

import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.extension.e2e.junit.edc.EmbeddedRuntimeFixed;
import org.eclipse.edc.connector.controlplane.contract.spi.offer.ContractDefinitionResolver;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.POLICY_DEFINITION_ID;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class AlwaysTruePolicyExtensionTest {

    @RegisterExtension
    static CeIntegrationTestExtension extension = CeIntegrationTestExtension.builder()
        .skipDb(true)
        .build();

    @BeforeEach
    void setUp(EmbeddedRuntimeFixed runtime) {
        runtime.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        runtime.registerServiceMock(
            DataPlaneInstanceStore.class,
            mock(DataPlaneInstanceStore.class));
    }

    @Test
    void alwaysTruePolicyDef(
        PolicyEngine policyEngine,
        PolicyDefinitionService policyDefinitionService
    ) {
        // arrange
        var alwaysTrue = alwaysTruePolicy(policyDefinitionService);

        // act
        var result = policyEngine.evaluate(
            ContractDefinitionResolver.CATALOGING_SCOPE,
            alwaysTrue.getPolicy(),
            mock(PolicyContext.class)
        );

        // assert
        assertTrue(result.succeeded(), "Always True Policy wasn't true.");
    }

    @NotNull
    private PolicyDefinition alwaysTruePolicy(PolicyDefinitionService policyDefinitionService) {
        var alwaysTrue = policyDefinitionService.findById(POLICY_DEFINITION_ID);
        requireNonNull(alwaysTrue, "Policy Definition does not exist: " + POLICY_DEFINITION_ID);
        return alwaysTrue;
    }
}
