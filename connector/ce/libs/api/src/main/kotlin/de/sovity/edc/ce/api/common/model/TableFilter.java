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
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Specify how to filter, sort and paginate a table of items.")
public class TableFilter<SortableProperties extends Enum<?>> {
    @Nullable
    @Schema(description = "Query for filtering. If empty, show all entries", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String query;

    @Schema(description = "Page Index. Defaults to 0", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "0")
    private Integer page = 0;

    @Nullable
    @Schema(description = "Size of each page. If left out, show all entries", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pageSize;

    @Nullable
    @Schema(
        description = "Sort List by properties in order of appearance in the array. If empty, sort by creation date",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<TableColumnSort<SortableProperties>> sort;
}
