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

package de.sovity.edc.ext.wrapper.api.common.mappers.policy;

import de.sovity.edc.ext.wrapper.api.common.mappers.policy.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import org.assertj.core.api.Assertions;
import org.eclipse.edc.policy.model.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorMapperTest {
    OperatorMapper operatorMapper;

    @BeforeEach
    void setup() {
        operatorMapper = new OperatorMapper();
    }

    @Test
    void test_getOperator() {
        Arrays.stream(OperatorDto.values()).forEach(dto -> {
            Operator operator = operatorMapper.getOperator(dto);
            assertThat(operator.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void test_getOperatorDto_OperatorDto() {
        Arrays.stream(Operator.values()).forEach(op -> {
            var dto = operatorMapper.getOperatorDto(op);
            assertThat(op.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void test_getOperatorDto_String() {
        Arrays.stream(Operator.values()).forEach(op -> {
            var dto = operatorMapper.getOperatorDto(op.name());
            assertThat(op.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void test_getOperatorDto_String_caseInsensitivity() {
        String operatorString = "eQ";
        OperatorDto operatorDto = operatorMapper.getOperatorDto(operatorString);
        Assertions.assertThat(operatorDto).isEqualTo(OperatorDto.EQ);
    }
}

