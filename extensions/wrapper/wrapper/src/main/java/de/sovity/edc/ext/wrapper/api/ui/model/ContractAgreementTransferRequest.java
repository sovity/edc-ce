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

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Required data for starting a Contract Agreement's Transfer Process")
public class ContractAgreementTransferRequest {
    @Schema(description = "Type of request", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementTransferRequestType type;

    @Schema(description = "For type PARAMS_ONLY: Required data for starting a Transfer Process")
    private ContractAgreementTransferRequestParams params;

    @Schema(description = "For type CUSTOM_JSON: Custom Transfer Process Create Dto JSON")
    private String customJson;
}
