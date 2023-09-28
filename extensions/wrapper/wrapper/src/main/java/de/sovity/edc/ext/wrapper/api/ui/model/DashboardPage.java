package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class DashboardPage {

    @Schema(description = "Providing Transfer Process Amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private DashboardTransferAmounts providingTransferProcesses;

    @Schema(description = "Consuming Transfer Process Amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private DashboardTransferAmounts consumingTransferProcesses;

    @Schema(description = "Asset amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private int numberOfAssets;

    @Schema(description = "Policy amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private int numberOfPolicies;

    @Schema(description = "Consuming Contract Agreement amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfConsumingAgreements;

    @Schema(description = "Providing Contract Agreement amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfProvidingAgreements;

    @Schema(description = "Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;
}
