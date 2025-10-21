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
import de.sovity.edc.ce.api.common.model.PaginationRequest;
import de.sovity.edc.ce.api.common.model.SortByRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Properties to filter and sort the contracts page")
public class ContractsPageRequest {
    @Nullable
    @Schema(description = "Filter for the termination-status of the contract", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ContractTerminationStatus terminationStatus;

    @Nullable
    @Schema(description = "Pagination", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PaginationRequest pagination;

    @Nullable
    @Schema(description = "Query for filtering. If empty, show all entries", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String searchText;

    @Nullable
    @Schema(
        description = "Sort List by properties in order of appearance in the array.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SortByRequest<ContractsPageSortProperty>> sortBy;
}
