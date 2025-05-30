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
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Contract Definition List Entry for Contract Definition Page", deprecated = true)
@Deprecated
public class ContractDefinitionEntry {

    @Schema(description = "Contract Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractDefinitionId;

    @Schema(description = "Access Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessPolicyId;

    @Schema(description = "Contract Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractPolicyId;

    @Schema(description = "Criteria for the contract", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiCriterion> assetSelector;
}
