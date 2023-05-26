package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TransferHistoryEntryContractAgreementDetails {

    @Schema(description = "Contract Agreements Start Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractStartDate;

    @Schema(description = "Contract Agreements End Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractEndDate;

    @Schema(description = "Incoming vs Outgoing", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementDirection direction;

    @Schema(description = "Other Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyAddress;

    @Schema(description = "Other Connector's ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyId;

    @Schema(description = "Asset Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetName;

    @Schema(description = "Contract Policy Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractPolicyName;
}
