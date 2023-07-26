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

import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.sovity.edc.client.ContractDefinitionTestUtils.createContractDefinition;

import java.text.ParseException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractDefinitionPageApiServiceTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
    }

    @Test
    void startContractDefinition(ContractDefinitionService contractDefinitionService) throws ParseException {
        var client = TestUtils.edcClient();

        // arrange
        createContractDefinition(contractDefinitionService);


        // act
        var result = client.uiApi().contractDefinitionPage();

        // Get the contract definition
        var contractDefinitions = result.getContractDefinitions();

        // assert
        var contractDefinition = contractDefinitions.get(0);
        assertThat(contractDefinition.getContractDefinitionId()).isEqualTo(ContractDefinitionTestUtils.CONTRACT_DEFINITION_ID);
        assertThat(contractDefinition.getContractPolicyId()).isEqualTo(ContractDefinitionTestUtils.CONTRACT_POLICY_ID);
        assertThat(contractDefinition.getAccessPolicyId()).isEqualTo(ContractDefinitionTestUtils.ACCESS_POLICY_ID);
    }


}
