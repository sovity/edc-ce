package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Filter expression for catalog filtering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
public class CatalogFilterExpression {
    @Schema(description = "Left Operand", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operandLeft;

    @Schema(description = "Operator", requiredMode = Schema.RequiredMode.REQUIRED)
    private CatalogFilterExpressionOperator operator;

    @Schema(description = "Right Operand", requiredMode = Schema.RequiredMode.REQUIRED)
    private CatalogFilterExpressionLiteral operandRight;
}
