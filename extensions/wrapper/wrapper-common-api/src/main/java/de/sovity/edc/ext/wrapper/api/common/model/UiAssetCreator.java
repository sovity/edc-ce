package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Asset Creator Details")
public class UiAssetCreator {

    @JsonProperty("@type")
    @Schema(description = "Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("http://xmlns.com/foaf/0.1/name")
    @Schema(description = "Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
