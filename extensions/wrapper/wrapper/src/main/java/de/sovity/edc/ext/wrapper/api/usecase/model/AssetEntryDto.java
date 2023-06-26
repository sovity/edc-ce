package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AssetEntryDto {
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private String assetRequestId;
    private Map<String, Object> assetRequestProperties;
    @Schema(description = "At least a property 'type' must be set", requiredMode = RequiredMode.REQUIRED)
    private Map<String, String> dataAddressProperties;
}
