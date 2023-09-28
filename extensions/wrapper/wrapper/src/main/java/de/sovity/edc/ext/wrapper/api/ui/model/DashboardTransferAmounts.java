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
public class DashboardTransferAmounts {

    @Schema(description = "Running Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numRunning;

    @Schema(description = "Completed Transfer Process Amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numOk;

    @Schema(description = "Number of Failed Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numError;
}
