package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionDto;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralDto;
import de.sovity.edc.ext.wrapper.api.ui.model.OperatorDto;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.query.Criterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

public class CriterionMapperTest {
    private CriterionMapper criterionMapper;
    private OperatorMapper operatorMapper;

    @BeforeEach
    void setup() {
        operatorMapper = new OperatorMapper();
        criterionMapper = new CriterionMapper(operatorMapper);
    }

    @Test
    void testMappingFromCriterionToDto() {
        Criterion criterion = new Criterion("operandLeft", "EQ", "operandRight");
        UiCriterionDto dto = criterionMapper.mapToCriterionDto(criterion);

        assertThat(dto.getOperandLeft()).isEqualTo(criterion.getOperandLeft());
        assertThat(dto.getOperator()).isEqualTo(operatorMapper.fromString(criterion.getOperator()));
        assertThat(dto.getOperandRight().getValue()).isEqualTo(criterion.getOperandRight());
    }

    @Test
    void testMappingFromDtoToCriterion() {
        UiCriterionDto dto = new UiCriterionDto();
        dto.setOperandLeft("operandLeft");
        dto.setOperator(OperatorDto.EQ);
        dto.setOperandRight(UiCriterionLiteralDto.ofValue("operandRight"));

        Criterion criterion = criterionMapper.mapToCriterion(dto);

        assertThat(criterion.getOperandLeft()).isEqualTo(dto.getOperandLeft());
        assertThat(criterion.getOperator()).isEqualTo(Operator.EQ.getOdrlRepresentation());
        assertThat(criterion.getOperandRight()).isEqualTo(dto.getOperandRight());
    }

    @Test
    void testBuildCriterionLiteral() {
        String value = "testValue";
        UiCriterionLiteralDto dtowithValue = criterionMapper.buildCriterionLiteral(value);

        List<Object> valueList = Arrays.asList("value1", "value2", null);
        UiCriterionLiteralDto dtowithList = criterionMapper.buildCriterionLiteral(valueList);

        assertThat(dtowithValue.getType()).isEqualTo(UiCriterionLiteralDto.CriterionLiteralTypeDto.VALUE);
        assertThat(dtowithValue.getValue()).isEqualTo(value);
        assertThat(dtowithValue.getValueList()).isNull();

        assertThat(dtowithList.getType()).isEqualTo(UiCriterionLiteralDto.CriterionLiteralTypeDto.VALUE_LIST);
        assertThat(dtowithList.getValueList()).containsExactly("value1", "value2", null);
        assertThat(dtowithList.getValue()).isNull();
    }

    @Test
    void testReadCriterionLiteral() {
        String value = "testValue";
        UiCriterionLiteralDto dtowithValue = UiCriterionLiteralDto.ofValue(value);

        List<String> valueList = Arrays.asList("value1", "value2");
        UiCriterionLiteralDto dtowithList = UiCriterionLiteralDto.ofValueList(valueList);

        assertThat(criterionMapper.readCriterionLiteral(dtowithValue)).isEqualTo(value);
        assertThat(criterionMapper.readCriterionLiteral(dtowithList)).isEqualTo(valueList);

    }
}

