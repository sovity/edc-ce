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
import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Schema(description = "Asset details", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiAsset asset;

    @Schema(description = "Contract Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicy contractPolicy;

    @Schema(description = "Contract Agreement's Transfer Processes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContractAgreementTransferProcess> transferProcesses;

    @Schema(description = "Contract Agreement's Termination Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractTerminationStatus terminationStatus;

    @Schema(description = "Contract Agreement's Metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ContractAgreementTerminationInfo terminationInformation;
}
