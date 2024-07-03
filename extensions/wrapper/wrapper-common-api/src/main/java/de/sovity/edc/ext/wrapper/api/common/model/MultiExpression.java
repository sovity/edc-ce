package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Policy Expression that can include multiple constraints as supported by our UI")
public class MultiExpression {

    @Schema(description = "The type of expression, e.g., ATOMIC_CONSTRAINT, AND, OR, XOR.", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultiExpressionType expressionType;

    @Schema(description =
        "A single atomic constraint. Will be evaluated if the expressionType is set to " +
            "ATOMIC_CONSTRAINT.")
    private AtomicConstraintDto atomicConstraint;

    @Schema(description = "Left expression for non atomic constraints (AND, OR, XOR)")
    private MultiExpression leftExpression;

    @Schema(description = "Right expression for non atomic constraints (AND, OR, XOR)")
    private MultiExpression rightExpression;

}
