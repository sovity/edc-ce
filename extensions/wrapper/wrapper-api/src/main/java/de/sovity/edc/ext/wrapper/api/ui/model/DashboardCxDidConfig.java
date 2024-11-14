package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "Managed Identity Wallet (MIW) Config")
public class DashboardCxDidConfig {
    @Schema(description = "My DID / edc.iam.issuer.id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String myDid;

    @Schema(description = "Wallet Token Url / edc.iam.sts.oauth.token.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String walletTokenUrl;

    @Schema(description = "Trusted VC Issuer / edc.iam.trusted-issuer.cofinity.id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trustedVcIssuer;

    @Schema(description = "BDRS Url / tx.iam.iatp.bdrs.server.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bdrsUrl;

    @Schema(description = "STS DIM Url / edc.iam.sts.dim.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dimUrl;
}
