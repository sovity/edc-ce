package de.sovity.edc.ext.wrapper.api.ui.model;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Operator for Criterion")
public enum OperatorEnumDto {
    EQ,
    NEQ,
    GT,
    GEQ,
    LT,
    LEQ,
    IN
}



