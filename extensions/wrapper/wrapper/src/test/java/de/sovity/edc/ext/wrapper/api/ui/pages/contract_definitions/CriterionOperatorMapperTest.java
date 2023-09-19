package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CriterionOperatorMapperTest {
    private CriterionOperatorMapper criterionOperatorMapper;

    @BeforeEach
    void setup() {
        criterionOperatorMapper = new CriterionOperatorMapper();
    }

    @Test
    void testCaseInsensitivity() {
        assertThat(criterionOperatorMapper.getUiCriterionOperator("lIKe")).isEqualTo(UiCriterionOperator.LIKE);
    }

    @Test
    void testMappings() {
        assertThat(criterionOperatorMapper.getUiCriterionOperator("=")).isEqualTo(UiCriterionOperator.EQ);
        assertThat(criterionOperatorMapper.getUiCriterionOperator("like")).isEqualTo(UiCriterionOperator.LIKE);
        assertThat(criterionOperatorMapper.getUiCriterionOperator("in")).isEqualTo(UiCriterionOperator.IN);
        assertThat(criterionOperatorMapper.getCriterionOperator(UiCriterionOperator.EQ)).isEqualTo("=");
        assertThat(criterionOperatorMapper.getCriterionOperator(UiCriterionOperator.LIKE)).isEqualTo("like");
        assertThat(criterionOperatorMapper.getCriterionOperator(UiCriterionOperator.IN)).isEqualTo("in");

        // Ensures the mapping isn't forgotten in the future
        Arrays.stream(UiCriterionOperator.values()).forEach(criterionOperatorMapper::getCriterionOperator);
    }

}

