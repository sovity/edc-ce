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

package de.sovity.edc.client;

import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.spi.query.Criterion;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractDefinitionTestUtils {

    public static final String CONTRACT_DEFINITION_ID = "contract-definition:eb934d1f-6582-4bab-85e6-af19a76f7e2b";
    public static final String CONTRACT_POLICY_ID = "contract-policy:f52a5d30-6356-4a55-a75a-3c45d7a88c3e";
    public static final String ACCESS_POLICY_ID = "access-policy:eb934d1f-6582-4bab-85e6-af19a76f7e2b";

    @NotNull
    public static void createContractDefinition(ContractDefinitionService contractDefinitionService) throws ParseException {

        List<Criterion> myCriteria = new ArrayList<>();
        // Add some criteria to the list
        myCriteria.add(new Criterion("exampleLeft1", "operator1", "exampleRight1"));
        myCriteria.add(new Criterion("exampleLeft2", "operator2", "exampleRight2"));
        var definition = ContractDefinition.Builder.newInstance().id(CONTRACT_DEFINITION_ID).contractPolicyId(CONTRACT_POLICY_ID).accessPolicyId(ACCESS_POLICY_ID).assetsSelector(myCriteria).build();
        contractDefinitionService.create(definition);
    }
    @NotNull
    public static void deleteContractDefinition(ContractDefinitionService contractDefinitionService) {
        contractDefinitionService.delete(CONTRACT_DEFINITION_ID);
    }

}
