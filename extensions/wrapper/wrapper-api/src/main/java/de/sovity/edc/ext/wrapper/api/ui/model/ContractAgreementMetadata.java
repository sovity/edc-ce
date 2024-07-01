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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contract's agreement metadata")
public class ContractAgreementMetadata {
    @Schema(description = "Cancellation's date and time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime cancellationDateTime;
    @Schema(description = "Cancellation's reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cancellationReason;
    @Schema(description = "Cancellation's detail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cancellationDetail;
}
