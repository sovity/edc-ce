/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */


package de.sovity.edc.client;

import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;

public class PolicyTestUtils {

    public static final String POLICY_DEFINITION_ID = "testPolicyDefinitionId1";


    public static void createPolicyDefinition(PolicyDefinitionService policyDefinitionService) {

        var policyDefinition = PolicyDefinition.Builder.newInstance().id(POLICY_DEFINITION_ID).build();
        policyDefinitionService.create(policyDefinition);
    }


}
