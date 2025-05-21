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

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "For type PARAMS_ONLY: Required data for starting a Transfer Process", deprecated = true)
@Deprecated
public class InitiateTransferRequest {
    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Transfer Type. Used to select a compatible DataPlane. " +
        "Examples are 'HttpData-PUSH', 'HttpData-PULL'. " +
        "Not to be confused with the 'type' of the data source, or the 'type' of the data sink found in the 'properties'",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String transferType;

    @Schema(description = "Data Sink / Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> dataSinkProperties;

    @Schema(description = "Additional transfer process properties. These are not passed to the consumer EDC", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> transferProcessProperties;
}
