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

    @Schema(description = "Number of Assets", requiredMode = Schema.RequiredMode.REQUIRED)
    private int numAssets;

    @Schema(description = "Number of Policies", requiredMode = Schema.RequiredMode.REQUIRED)
    private int numPolicies;

    @Schema(description = "Number of Contract Definitions", requiredMode = Schema.RequiredMode.REQUIRED)
    private int numContractDefinitions;

    @Schema(description = "Number of consuming Contract Agreements", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numContractAgreementsConsuming;

    @Schema(description = "Number of providing Contract Agreements", requiredMode = Schema.RequiredMode.REQUIRED)
    private long numContractAgreementsProviding;

    @Schema(description = "Consuming Transfer Process Amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private DashboardTransferAmounts transferProcessesConsuming;

    @Schema(description = "Providing Transfer Process Amounts", requiredMode = Schema.RequiredMode.REQUIRED)
    private DashboardTransferAmounts transferProcessesProviding;

    @Schema(description = "Your Connector's Connector Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Your Connector's Participant ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorParticipantId;

    @Schema(description = "Your Connector's Title", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorTitle;

    @Schema(description = "Your Connector's Description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorDescription;

    @Schema(description = "Your Organization Homepage", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorCuratorUrl;

    @Schema(description = "Your Organization Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorCuratorName;

    @Schema(description = "Your Connector's Maintainer's Organization Homepage", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorMaintainerUrl;

    @Schema(description = "Your Connector's Maintainer's Organization Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorMaintainerName;

    @Schema(description = "Your Connector's DAPS Configuration (if present)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DashboardDapsConfig connectorDapsConfig;

    @Schema(description = "Your Connector's MIW Configuration (if present)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DashboardMiwConfig connectorMiwConfig;
}
