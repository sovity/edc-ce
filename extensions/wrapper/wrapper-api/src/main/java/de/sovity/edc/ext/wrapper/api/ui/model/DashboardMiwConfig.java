package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "Managed Identity Wallet (MIW) Config")
public class DashboardMiwConfig {
    @Schema(description = "Your Connector's MIW's URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Your Connector's MIW's Token URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenUrl;

    @Schema(description = "Your Connector's MIW's Authority ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorityId;
}
