package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DashboardPage {

    @Schema(description = "Running Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long runningTransferProcesses;

    @Schema(description = "Completed Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long completedTransferProcesses;

    @Schema(description = "Interrupted Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long interruptedTransferProcesses;

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
