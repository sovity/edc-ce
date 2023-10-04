package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Schema(description = "Criterion Literal")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiCriterionLiteral {

    private UiCriterionLiteralType type;

    @Schema(description = "Only for type VALUE. The single value representation.")
    private String value;

    @Schema(description = "Only for type VALUE_LIST. List of values, e.g. for the IN-Operator.")
    private List<String> valueList;

    public static UiCriterionLiteral ofValue(@NonNull String value) {
        return new UiCriterionLiteral(UiCriterionLiteralType.VALUE, value, null);
    }

    public static UiCriterionLiteral ofValueList(@NonNull List<String> valueList) {
        return new UiCriterionLiteral(UiCriterionLiteralType.VALUE_LIST, null, valueList);
    }
}
