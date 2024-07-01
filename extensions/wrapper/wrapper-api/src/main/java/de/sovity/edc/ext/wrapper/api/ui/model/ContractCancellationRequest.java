/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

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
@Schema(description = "Data for cancelling a Contract Agreement")
public class ContractCancellationRequest {

    @Schema(title = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    String contractAgreementId;

    @Schema(
        title = "Cancellation reason",
        description = "A short reason why this contract was cancelled",
        maxLength = 64,
        requiredMode = Schema.RequiredMode.REQUIRED)
    String reason;

    @Schema(
        title = "Cancellation detail",
        description = "A user explanation to detail why the contract was cancelled.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String detail;
}

