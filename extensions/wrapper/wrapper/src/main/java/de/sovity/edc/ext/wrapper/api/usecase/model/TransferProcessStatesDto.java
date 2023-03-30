package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;

import java.util.Map;

@Data
public class TransferProcessStatesDto {
    @Schema(description = "States and count of incoming transferprocess counts", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<TransferProcessStates, Long> incomingTransferProcessCounts;

    @Schema(description = "States and counts of outgoing transferprocess counts", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<TransferProcessStates, Long> outgoingTransferProcessCounts;
}
