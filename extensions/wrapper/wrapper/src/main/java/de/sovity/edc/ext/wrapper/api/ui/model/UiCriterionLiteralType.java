package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Value type of an asset selector criterion right expression value", enumAsRef = true)
public enum UiCriterionLiteralType {
    VALUE, VALUE_LIST
}
