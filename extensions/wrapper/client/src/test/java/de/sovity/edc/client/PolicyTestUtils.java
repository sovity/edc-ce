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

import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;

import java.util.List;

public class PolicyTestUtils {

    public static final String POLICY_DEFINITION_ID = "testPolicyDefinitionId1";

    public static de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint buildPolicyConstraint(UiPolicyConstraint generatedconstraint) {
        var operatorMapper = new OperatorMapper();
        return de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint.builder()
                .left(generatedconstraint.getLeft())
                .operator(operatorMapper.getOperatorDto(generatedconstraint.getOperator().toString()))
                .right(buildPolicyLiteral(generatedconstraint.getRight()))
                .build();
    }

    public static de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral buildPolicyLiteral(UiPolicyLiteral generateduiPolicyLiteral) {
        return de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral.builder()
                .type(de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType.valueOf(generateduiPolicyLiteral.getType().toString()))
                .value(generateduiPolicyLiteral.getValue())
                .valueList(generateduiPolicyLiteral.getValueList())
                .build();
    }
    public static List<de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint> buildPolicyConstraints(List<UiPolicyConstraint> generatedConstraints) {
        return generatedConstraints.stream()
                .map(PolicyTestUtils::buildPolicyConstraint)
                .toList();
    }
}
