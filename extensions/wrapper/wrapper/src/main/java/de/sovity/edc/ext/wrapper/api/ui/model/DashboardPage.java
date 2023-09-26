package de.sovity.edc.ext.wrapper.api.ui.model;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class DashboardPage {

    @Schema(description = "Contract Agreement Cards", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContractAgreementCard> consumingContractAgreements;

    @Schema(description = "Contract Agreement Cards", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContractAgreementCard> providingContractAgreements;

    @Schema(description = "Transfer Process amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private int transferProcessAmounts;

    @Schema(description = "List of Assets", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiAsset> assets;

    @Schema(description = "List Of Policies", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiPolicy> policies;

    @Schema(description = "Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;
}
