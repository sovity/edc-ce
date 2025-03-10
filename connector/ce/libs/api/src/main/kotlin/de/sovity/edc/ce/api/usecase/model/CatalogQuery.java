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
@Schema(description = "Catalog query parameters")
public class CatalogQuery {
    @Schema(description = "Target EDC DSP endpoint URL. Can contain a queryParam 'participantId', which is provided by default in the " +
        "Connector Endpoint in the EDC UI.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Target EDC Participant ID. It is required if the connector endpoint does not contain the queryParam " +
        "'participantId'.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String participantId;

    @Schema(description = "Limit the number of results", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer limit;

    @Schema(description = "Offset for returned results, e.g. start at result 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer offset;

    @Schema(description = "Filter expressions for catalog filtering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CatalogFilterExpression> filterExpressions;
}
