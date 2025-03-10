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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contract's agreement metadata")
public class ContractAgreementTerminationInfo {

    @Schema(description = "Termination's date and time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime terminatedAt;

    @Schema(
        title = "Termination's reason",
        description = "The termination's nature e.g. User Termination",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(
        description = "Detailed message from the terminating party about why the contract was terminated.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String detail;

    @Schema(
        description = "Indicates whether the termination comes from this EDC or the counterparty EDC.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ContractTerminatedBy terminatedBy;
}
