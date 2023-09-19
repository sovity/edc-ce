package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterion;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionOperator;
import org.eclipse.edc.spi.query.Criterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CriterionMapperTest {
    private CriterionMapper criterionMapper;

    @BeforeEach
    void setup() {
        criterionMapper = new CriterionMapper(new CriterionOperatorMapper());
    }

    @Test
    void testMappingFromCriterionToDto() {
        Criterion criterion = new Criterion("operandLeft", "=", "operandRight");
        UiCriterion dto = criterionMapper.mapToCriterionDto(criterion);

        assertThat(dto.getOperandLeft()).isEqualTo(criterion.getOperandLeft());
        assertThat(dto.getOperator()).isEqualTo(UiCriterionOperator.EQ);
        assertThat(dto.getOperandRight().getValue()).isEqualTo(criterion.getOperandRight());
    }

    @Test
    void testMappingFromDtoToCriterion() {
        UiCriterion dto = new UiCriterion();
        dto.setOperandLeft("operandLeft");
        dto.setOperator(UiCriterionOperator.EQ);
        dto.setOperandRight(UiCriterionLiteral.ofValue("operandRight"));

        Criterion criterion = criterionMapper.mapToCriterion(dto);

        assertThat(criterion.getOperandLeft()).isEqualTo(dto.getOperandLeft());
        assertThat(criterion.getOperator()).isEqualTo("=");
        assertThat(criterion.getOperandRight()).isEqualTo(dto.getOperandRight());
    }

    @Test
    void testLiteralMapping_String() {
        String value = "testValue";
        UiCriterionLiteral literal = criterionMapper.buildCriterionLiteral(value);

        assertThat(literal.getType()).isEqualTo(UiCriterionLiteral.CriterionLiteralTypeDto.VALUE);
        assertThat(literal.getValue()).isEqualTo(value);
        assertThat(literal.getValueList()).isNull();
    }

    @Test
    void testLiteralMapping_StringList() {
        List<Object> valueList = Arrays.asList("value1", "value2", null);
        UiCriterionLiteral literal = criterionMapper.buildCriterionLiteral(valueList);

        assertThat(literal.getType()).isEqualTo(UiCriterionLiteral.CriterionLiteralTypeDto.VALUE_LIST);
        assertThat(literal.getValueList()).containsExactly("value1", "value2", null);
        assertThat(literal.getValue()).isNull();
    }

    @Test
    void testReadCriterionLiteral() {
        String value = "testValue";
        UiCriterionLiteral dtowithValue = UiCriterionLiteral.ofValue(value);

        List<String> valueList = Arrays.asList("value1", "value2");
        UiCriterionLiteral dtowithList = UiCriterionLiteral.ofValueList(valueList);

        assertThat(criterionMapper.readCriterionLiteral(dtowithValue)).isEqualTo(value);
        assertThat(criterionMapper.readCriterionLiteral(dtowithList)).isEqualTo(valueList);

    }
}

