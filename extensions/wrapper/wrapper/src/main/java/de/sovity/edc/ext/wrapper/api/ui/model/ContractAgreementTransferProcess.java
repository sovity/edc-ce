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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "A Contract Agreement's Transfer Process")
public class ContractAgreementTransferProcess {
    @Schema(description = "Transfer Process ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transferProcessId;
    @Schema(description = "Last Change Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastUpdatedDate;
    @Schema(description = "Current State", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessState state;
    @Schema(description = "Error Message")
    private String errorMessage;

}
