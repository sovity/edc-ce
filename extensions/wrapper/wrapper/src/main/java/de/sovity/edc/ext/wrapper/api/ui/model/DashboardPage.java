package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "Dashboard page data", requiredMode = Schema.RequiredMode.REQUIRED)
public class DashboardPage {
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
