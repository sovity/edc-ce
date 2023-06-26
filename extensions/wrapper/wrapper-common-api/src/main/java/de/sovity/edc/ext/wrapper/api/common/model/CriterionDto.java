package de.sovity.edc.ext.wrapper.api.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterionDto {

    @NotNull(message = "operandLeft cannot be null")
    private Object operandLeft;
    @NotNull(message = "operator cannot be null")
    private String operator;
    private Object operandRight;
}
