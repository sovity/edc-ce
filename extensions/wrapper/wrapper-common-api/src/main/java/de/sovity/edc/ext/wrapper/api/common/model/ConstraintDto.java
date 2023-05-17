package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Type-Safe OpenAPI generator friendly Constraint DTO that supports an opinionated" +
        " subset of the original EDC Constraint Entity.")
public class ConstraintDto {
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String leftExpression;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private OperatorDto operatorDto;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rightExpression;
}
