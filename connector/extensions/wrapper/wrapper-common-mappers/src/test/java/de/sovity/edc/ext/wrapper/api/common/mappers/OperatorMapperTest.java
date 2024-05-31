package de.sovity.edc.ext.wrapper.api.common.mappers;

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

