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

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Contract Agreement for Contract Agreement Page")
public class ContractAgreementCard {
    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Contract Negotiation ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractNegotiationId;

    @Schema(description = "Incoming vs Outgoing", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementDirection direction;

    @Schema(description = "Other Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyAddress;

    @Schema(description = "Other Connector's ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyId;

    @Schema(description = "Contract Agreements Signing Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractSigningDate;

    @Schema(description = "Contract Agreements Start Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractStartDate;

    @Schema(description = "Contract Agreements End Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractEndDate;

    @Schema(description = "Asset details", requiredMode = Schema.RequiredMode.REQUIRED)
    private AssetDto asset;

    @Schema(description = "Contract Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyDto contractPolicy;

    @Schema(description = "Contract Agreement's Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContractAgreementTransferProcess> transferProcesses;
}
