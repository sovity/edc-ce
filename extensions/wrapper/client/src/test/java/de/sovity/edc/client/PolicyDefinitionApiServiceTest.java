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


import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType;
import de.sovity.edc.ext.wrapper.api.usecase.services.PolicyMappingService;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static de.sovity.edc.client.PolicyTestUtils.POLICY_DEFINITION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
public class PolicyDefinitionApiServiceTest {
    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS = "http://some-other-connector/api/v1/ids/data";
    PolicyMapper policyMapper;
    ObjectMapper jsonLdObjectMapper;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        jsonLdObjectMapper = new ObjectMapper();
        policyMapper = new PolicyMapper(jsonLdObjectMapper);
    }

    @Test
    void policyDefinitionPage(PolicyDefinitionService policyDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var constraints =  List.of(UiPolicyConstraint.builder().build());
        var errors = List.of(
                "error1",
                "error2");

        var policyJsonLd = "{\n" +
                "  \"@type\": \"someTypeValue\",\n" +
                "  \"target\": \"someTargetValue\",\n" +
                "  \"inheritsFrom\": \"someInheritsFromValue\",\n" +
                "  \"permissions\": [\"permission1\", \"permission2\"],\n" +
                "  \"obligations\": [\"obligation1\", \"obligation2\"],\n" +
                "  \"duty\": \"someDutyValue\",\n" +
                "  \"prohibition\": \"someProhibitionValue\",\n" +
                "  \"prohibitions\": [\"prohibition1\", \"prohibition2\"],\n" +
                "  \"assigner\": \"someAssignerValue\",\n" +
                "  \"extensibleProperties\": {\n" +
                "    \"property1\": \"value1\",\n" +
                "    \"property2\": \"value2\"\n" +
                "  },\n" +
                "  \"permission\": \"somePermissionValue\",\n" +
                "  \"assignee\": \"someAssigneeValue\"\n" +
                "}\n";
        createPolicyDefinition(policyDefinitionService, policyJsonLd, constraints, errors);

        //act
        var result = client.uiApi().policyPage();

        //assert
        var policyDefinitions = result.getPolicies();
        assertThat(policyDefinitions).hasSize(1);
        var policyDefinition = policyDefinitions.get(0);
        assertThat(policyDefinition.getUiPolicyDto().getPolicyJsonLd()).isEqualTo("policyJsonLd-1");
        assertThat(policyDefinition.getUiPolicyDto().getConstraints()).isEqualTo(constraints);
        assertThat(policyDefinition.getUiPolicyDto().getErrors()).isEqualTo(errors);
    }

    @Test
    void policyDefinitionPageSorting(PolicyDefinitionService policyDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        createPolicyDefinition(
                policyDefinitionService,
                "policyJsonLd-1",
                List.of(UiPolicyConstraint.builder().build()),
                List.of("error1"));
        createPolicyDefinition(policyDefinitionService,
                "policyJsonLd-2",
                List.of(UiPolicyConstraint.builder().build()),
                List.of("error2"));
        createPolicyDefinition(policyDefinitionService,
                "policyJsonLd-3",
                List.of(UiPolicyConstraint.builder().build()),
                List.of("error3"));

        //act
        var result = client.uiApi().policyPage();

        //assert
        var policyDefinitions = result.getPolicies();
        assertThat(policyDefinitions).hasSize(3);
        assertThat(result.getPolicies())
                .extracting(policyDefinition -> policyDefinition.getUiPolicyDto().getErrors()).containsExactly(
                List.of("error1"),
                List.of("error2"),
                List.of("error3"));
    }

    private void createPolicyDefinition(PolicyDefinitionService policyDefinitionService, String policyJsonLd, List<UiPolicyConstraint> constraints, List<String> errors) {
        UiPolicyDto uiPolicyDto = UiPolicyDto.builder()
                .policyJsonLd(policyJsonLd)
                .constraints(constraints)
                .errors(errors)
                .build();

        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .policy(policyMapper.buildPolicy(uiPolicyDto))
                .build();
        policyDefinitionService.create(policyDefinition);
    }
}
