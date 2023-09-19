package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterion;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionOperator;
import org.eclipse.edc.spi.query.Criterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriterionMapperTest {
    private CriterionMapper criterionMapper;

    @BeforeEach
    void setup() {
        criterionMapper = new CriterionMapper(new CriterionOperatorMapper(), new CriterionLiteralMapper());
    }

    @Test
    void testMappingFromCriterionToDto() {
        Criterion criterion = new Criterion("left", "=", "right");
        UiCriterion dto = criterionMapper.buildUiCriterion(criterion);

        assertThat(dto.getOperandLeft()).isEqualTo("left");
        assertThat(dto.getOperator()).isEqualTo(UiCriterionOperator.EQ);
        assertThat(dto.getOperandRight().getValue()).isEqualTo("right");
    }

    @Test
    void testMappingFromDtoToCriterion() {
        UiCriterion dto = new UiCriterion();
        dto.setOperandLeft("left");
        dto.setOperator(UiCriterionOperator.EQ);
        dto.setOperandRight(UiCriterionLiteral.ofValue("right"));

        Criterion criterion = criterionMapper.buildCriterion(dto);

        assertThat(criterion.getOperandLeft()).isEqualTo("left");
        assertThat(criterion.getOperator()).isEqualTo("=");
        assertThat(criterion.getOperandRight()).isEqualTo("right");
    }
}

