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
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Sort a column by a given property")
public class TablePage<ContentType> {
    @Schema(description = "Content of the current page.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContentType> content;

    @Schema(description = "Number of items on all pages.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalItems;

    @Schema(description = "Index of the last page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lastPage;

    @Nullable
    @Schema(description = "Index of the previous page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer previousPage;

    @Schema(description = "Index of the current page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer currentPage;

    @Nullable
    @Schema(description = "Index of the next page.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer nextPage;

    @Schema(description = "Index of the item at the start of the current page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageStart;

    @Schema(description = "Index of the item at the end of the current page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageEnd;

    @Schema(description = "Index of the next page.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
}
