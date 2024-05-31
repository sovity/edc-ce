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

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "For type PARAMS_ONLY: Required data for starting a Transfer Process")
public class InitiateTransferRequest {
    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Data Sink / Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> dataSinkProperties;

    @Schema(description = "Additional transfer process properties. These are not passed to the consumer EDC", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> transferProcessProperties;
}
