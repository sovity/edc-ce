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
@Schema(description = "ODRL AtomicConstraint as supported by our UI")
public class UiPolicyMultiplicityConstraint {
    @Schema(description = "Left side of the expression.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String left;

    @Schema(description = "Operator, e.g. EQ", requiredMode = Schema.RequiredMode.REQUIRED)
    private OperatorDto operator;

    @Schema(description = "Right side of the expression", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicyLiteral right;

    @Schema(description = "Sub-constraints for nested policies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UiPolicyConstraint> subConstraints;
}
