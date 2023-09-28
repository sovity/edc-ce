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

    @Schema(description = "Running Providing Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfRunningProvidingTransferProcesses;

    @Schema(description = "Completed Providing Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfCompletedProvidingTransferProcesses;

    @Schema(description = "Interrupted Providing Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfInterruptedProvidingTransferProcesses;

    @Schema(description = "Running Consuming Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfRunningConsumingTransferProcesses;

    @Schema(description = "Completed Consuming Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfCompletedConsumingTransferProcesses;

    @Schema(description = "Interrupted Consuming Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numberOfInterruptedConsumingTransferProcesses;

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
