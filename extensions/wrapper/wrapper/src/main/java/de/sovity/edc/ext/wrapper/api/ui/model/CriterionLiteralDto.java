package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Schema(description = "Criterion Literal DTO")
public class CriterionLiteralDto {

    public enum CriterionLiteralTypeDto {
        VALUE, VALUE_LIST
    }

    private final CriterionLiteralTypeDto type;

    @Schema(description = "Only for type VALUE. The single value representation.")
    private final String value;

    @Schema(description = "Only for type VALUE_LIST. List of values, e.g. for the IN-Operator.")
    private final List<String> valueList;

    private CriterionLiteralDto(CriterionLiteralTypeDto type, String value, List<String> valueList) {
        this.type = type;
        this.value = value;
        this.valueList = valueList;
    }

    public static CriterionLiteralDto ofValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null for VALUE type.");
        }
        return new CriterionLiteralDto(CriterionLiteralTypeDto.VALUE, value, null);
    }

    public static CriterionLiteralDto ofValueList(List<String> valueList) {
        if (valueList == null) {
            throw new IllegalArgumentException("ValueList cannot be null for VALUE_LIST type.");
        }
        return new CriterionLiteralDto(CriterionLiteralTypeDto.VALUE_LIST, null, valueList);
    }
}
