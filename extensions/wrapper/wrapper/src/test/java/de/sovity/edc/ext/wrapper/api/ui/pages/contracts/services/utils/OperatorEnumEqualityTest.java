
package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;

import de.sovity.edc.ext.wrapper.api.ui.model.OperatorDto;
import org.eclipse.edc.policy.model.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
public class OperatorEnumEqualityTest {
    OperatorMapper operatorMapper;

    @BeforeEach
    void setup() {
        operatorMapper = new OperatorMapper();
    }

    @Test
    void MappingFromOperatorToDto() {
        Arrays.stream(Operator.values()).forEach(operator -> {
            OperatorDto dto = operatorMapper.toOperatorDto(operator);
            assertThat(dto.name()).isEqualTo(operator.name());
        });
    }

    @Test
    void MappingFromDtoToOperator() {
        Arrays.stream(OperatorDto.values()).forEach(dto -> {
            Operator operator = operatorMapper.toOperator(dto);
            assertThat(operator.name()).isEqualTo(dto.name());
        });
    }

    @Test
    void testCaseInsensitivityFromString() {
        String operatorString = "Eq";
        OperatorDto operatorDto = operatorMapper.fromString(operatorString);
        assertThat(operatorDto).isEqualTo(OperatorDto.EQ);
    }
}

