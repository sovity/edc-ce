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
import de.sovity.edc.ce.api.common.model.AssetFilterConstraint;
import de.sovity.edc.ce.api.common.model.CallbackAddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Negotiates all given assets by the given filter")
public class NegotiateAllQuery {
    @Schema(description = "Target EDC DSP endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Target EDC Participant ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;

    @Schema(description = "Data offers to negotiate. Filter expressions, joined by AND. If left empty, all data offers are negotiated", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AssetFilterConstraint> filter;

    @Schema(description = "Optional limit to limit the number of assets negotiated to", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer limit;

    @Schema(description = "Optionally listen to negotiation success / failure for new negotiations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CallbackAddressDto> callbackAddresses;
}
