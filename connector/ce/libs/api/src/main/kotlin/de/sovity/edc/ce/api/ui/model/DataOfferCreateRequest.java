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

import de.sovity.edc.ce.api.common.model.DataOfferPolicyCreateType;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Request to create a data offer")
public class DataOfferCreateRequest {

    @Schema(description = "The asset to create", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiAssetCreateRequest asset;

    @Schema(description = "The data offer publishing type", requiredMode = Schema.RequiredMode.REQUIRED)
    private DataOfferPublishType publishType;

    @Schema(description = "The policy creation type. Only required if publishType is PUBLISH_RESTRICTED.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DataOfferPolicyCreateType policyCreateType;

    @Schema(description = "Tree-structured policy expression. Only required if publishType is PUBLISH_RESTRICTED and policyCreateType is POLICY_EXPRESSION.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UiPolicyExpression policyExpression;

    @Schema(description = "ODRL policy as JSON-LD. Only required if publishType is PUBLISH_RESTRICTED and policyCreateType is POLICY_JSON_LD.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String policyJsonLd;
}
