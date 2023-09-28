package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "DAPS Config")
public class DashboardDapsConfig {
    @Schema(description = "Your Connector's DAPS Token URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenUrl;

    @Schema(description = "Your Connector's DAPS JWKS URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jwksUrl;
}
