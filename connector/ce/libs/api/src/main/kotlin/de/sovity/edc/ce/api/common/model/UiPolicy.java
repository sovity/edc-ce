/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
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
 *     Fraunhofer ISST - initial implementation of an unified workflow for creating data offerings
 */
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
@Schema(description = "Type-Safe OpenAPI generator friendly ODLR policy subset as endorsed by sovity.")
public class UiPolicy {
    @Schema(description = "EDC Policy JSON-LD. This is required because the EDC requires the " +
        "full policy when initiating contract negotiations.", requiredMode = RequiredMode.REQUIRED)
    private String policyJsonLd;

    @Schema(description = "Policy expression")
    private UiPolicyExpression expression;

    @Schema(description = "When trying to reduce the policy JSON-LD to our opinionated subset of functionalities, " +
        "many fields and functionalities are unsupported. Should any discrepancies occur during " +
        "the mapping process, we'll collect them here.", requiredMode = RequiredMode.REQUIRED)
    private List<String> errors;
}
