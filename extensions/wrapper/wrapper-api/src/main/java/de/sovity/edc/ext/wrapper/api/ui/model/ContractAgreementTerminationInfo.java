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
public class ContractAgreementTerminationInfo {
    
    @Schema(description = "Contract Agreement's Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractTerminationStatus terminationStatus;

    @Schema(description = "Termination's date and time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime terminationDateTime;

    @Schema(description = "Termination's reason", requiredMode = Schema.RequiredMode.REQUIRED)
    private String terminationReason;

    @Schema(
        description = "Detailed message from the terminating party on why the contract was terminated.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String terminationDetail;
}
