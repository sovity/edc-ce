package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Criterion Literal DTO")
public class CriterionLiteralDto {

    @Schema(description = "")
    private String value;

    @Schema(description = "")
    private List<String> valueList;
    @Override
    public String toString() {
        return "class CriterionLiteralDto {\n    value: " + value + "\n    valueList: " + valueList + "\n}";
    }
}

