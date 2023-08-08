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

package de.sovity.edc.client;

import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static de.sovity.edc.client.PolicyTestUtils.createPolicyDefinition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
public class PolicyDefinitionApiServiceTest {
    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS = "http://some-other-connector/api/v1/ids/data";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
    }

    @Test
    void getPolicyDefinition(PolicyDefinitionService policyDefinitionService) {
        var client = TestUtils.edcClient();

        createPolicyDefinition(policyDefinitionService);
        var result = client.uiApi().policyPage();

        // get
        var policyDefinition = result.getPolicies();

        // assert for the order of entries
        assertThat(policyDefinition.get(1).getPolicyDefinitionId()).isEqualTo(PolicyTestUtils.POLICY_DEFINITION_ID);


    }


    //create PD and
    //core edc policy definition build and get

}
