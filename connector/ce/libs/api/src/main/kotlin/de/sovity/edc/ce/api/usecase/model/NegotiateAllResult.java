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
package de.sovity.edc.ce.api.usecase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiPolicy;
import de.sovity.edc.ce.api.ui.model.ContractNegotiationState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Negotiations that either already exist or were started")
public class NegotiateAllResult {
    @Schema(description = "Negotiation ID (either existing or new if no active contract was found)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractNegotiationId;

    @Schema(description = "Contract Agreement ID (will only be ever present if an active old negotiation was found)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Contract Negotiation State", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractNegotiationState state;

    @Schema(description = "Contract Negotiation State Last Change", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime stateChangedAt;

    @Schema(description = "Asset that was negotiated. Will not contain asset metadata for already negotiated offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiAsset asset;

    @Schema(description = "Contract Offer that was negotiated. Will not contain asset metadata for already negotiated offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicy policy;
}
