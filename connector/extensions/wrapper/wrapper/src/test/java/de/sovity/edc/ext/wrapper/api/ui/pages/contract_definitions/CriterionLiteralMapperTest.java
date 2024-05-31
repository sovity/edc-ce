package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CriterionLiteralMapperTest {
    private CriterionLiteralMapper criterionLiteralMapper;

    @BeforeEach
    void setup() {
        criterionLiteralMapper = new CriterionLiteralMapper();
    }

    @Test
    void testBuildUiCriterionLiteral_String() {
        String value = "testValue";
        UiCriterionLiteral literal = criterionLiteralMapper.buildUiCriterionLiteral(value);

        assertThat(literal.getType()).isEqualTo(UiCriterionLiteralType.VALUE);
        assertThat(literal.getValue()).isEqualTo(value);
        assertThat(literal.getValueList()).isNull();
    }

    @Test
    void testBuildUiCriterionLiteral_StringList() {
        List<?> valueList = Arrays.asList("value1", "value2", null);
        UiCriterionLiteral literal = criterionLiteralMapper.buildUiCriterionLiteral(valueList);

        assertThat(literal.getType()).isEqualTo(UiCriterionLiteralType.VALUE_LIST);
        assertThat(literal.getValueList()).containsExactly("value1", "value2", null);
        assertThat(literal.getValue()).isNull();
    }

    @Test
    void testGetValue_String() {
        String value = "testValue";
        UiCriterionLiteral literal = UiCriterionLiteral.ofValue(value);
        assertThat(criterionLiteralMapper.getValue(literal)).isEqualTo(value);
    }

    @Test
    void testGetValue_StringList() {
        List<String> valueList = Arrays.asList("value1", "value2");
        UiCriterionLiteral literal = UiCriterionLiteral.ofValueList(valueList);
        assertThat(criterionLiteralMapper.getValue(literal)).isEqualTo(valueList);
    }
}

