
package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;

import de.sovity.edc.ext.wrapper.api.ui.model.OperatorDto;
import org.eclipse.edc.policy.model.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

class OperatorMapperTest {
    OperatorMapper operatorMapper;

    @BeforeEach
    void setup() {
        operatorMapper = new OperatorMapper();
    }

    @Test
    void testMappingFromDtoToOperator() {
        Arrays.stream(OperatorDto.values()).forEach(dto -> {
            Operator operator = operatorMapper.toOperator(dto);
            assertThat(operator.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void testMappingFromString() {
        Arrays.stream(Operator.values()).forEach(op -> {
            var dto = operatorMapper.fromString(op.name());
            assertThat(op.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void testCaseInsensitivityFromString() {
        String operatorString = "Eq";
        OperatorDto operatorDto = operatorMapper.fromString(operatorString);
        assertThat(operatorDto).isEqualTo(OperatorDto.EQ);
    }
}

