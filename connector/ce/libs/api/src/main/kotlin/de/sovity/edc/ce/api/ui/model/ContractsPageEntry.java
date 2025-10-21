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
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Contracts Page Entry")
public class ContractsPageEntry {
    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Incoming vs Outgoing", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementDirection direction;

    @Schema(description = "Other Connector's ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyId;

    @Schema(description = "Contract Agreements Signing Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime contractSigningDate;

    @Schema(description = "Asset ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetId;

    @Schema(description = "Asset title", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetTitle;

    @Schema(description = "Number of transfer processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer transferProcessesCount;

    @Schema(description = "Contract Agreement's Termination Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractTerminationStatus terminationStatus;

    @Schema(description = "Contract Terminated At", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime terminatedAt;
}
