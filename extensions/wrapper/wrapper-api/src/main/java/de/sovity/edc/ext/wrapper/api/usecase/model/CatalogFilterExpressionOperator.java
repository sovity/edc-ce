package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Operator for filter expressions", enumAsRef = true)
public enum CatalogFilterExpressionOperator {
    LIKE, EQ, IN
}
