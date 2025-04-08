/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.api.ui.model;

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
@Schema(description = "Data as required by the UI's Dashboard Page")
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

    @Schema(description = "Your Connector's Management API URL for API Users", requiredMode = Schema.RequiredMode.REQUIRED)
    private String managementApiUrlShownInDashboard;

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

    @Schema(description = "Your Connector's Catena-X Web-DID Configuration (if present)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DashboardCxDidConfig connectorCxDidConfig;
}
