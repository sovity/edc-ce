package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Policy Expression that can include multiple constraints as supported by our UI")
public class UiPolicyExpression {

    @Schema(description = "Either ATOMIC_CONSTRAINT or one of the multiplicity constraint types.")
    private ExpressionType expressionType;

    @Schema(description =
        "List of policy elements that are evaluated according the expressionType.")
    private List<Expression> expressions; //

    @Schema(description =
        "A single atomic constraint. Will be evaluated if the expressionType is set to " +
            "ATOMIC_CONSTRAINT.")
    private AtomicConstraintDto atomicConstraint;

}
