package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Number of Transfer Processes for given direction.")
public class DashboardTransferAmounts {
    @Schema(description = "Number of Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numTotal;

    @Schema(description = "Number of running Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numRunning;

    @Schema(description = "Number of successful Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numOk;

    @Schema(description = "Number of failed Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numError;
}
