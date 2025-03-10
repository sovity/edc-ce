/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.policy;

import de.sovity.edc.ce.api.common.model.OperatorDto;
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

