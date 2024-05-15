package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Value type of an asset selector criterion right expression value", enumAsRef = true)
public enum CatalogFilterExpressionLiteralType {
    VALUE, VALUE_LIST
}
