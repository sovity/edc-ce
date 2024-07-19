package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DAPS Config")
public class DashboardDapsConfig {
    @Schema(description = "Your Connector's DAPS Token URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenUrl;

    @Schema(description = "Your Connector's DAPS JWKS URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jwksUrl;
}
