/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Transfer History Entry for Transfer History Page")
public class TransferHistoryEntry {
    @Schema(description = "Transfer Process ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transferProcessId;

    @Schema(description = "Created Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdDate;

    @Schema(description = "Last Change Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastUpdatedDate;

    @Schema(description = "Transfer History State", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessState state;

    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Incoming vs Outgoing", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementDirection direction;

    @Schema(description = "Other Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyConnectorEndpoint;

    @Schema(description = "Other Connector's Participant ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyParticipantId;

    @Schema(description = "Asset Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetName;

    @Schema(description = "Asset ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetId;

    @Schema(description = "Error Message")
    private String errorMessage;

}
