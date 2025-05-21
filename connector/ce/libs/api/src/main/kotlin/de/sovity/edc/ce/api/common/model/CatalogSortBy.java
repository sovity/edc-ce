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
import de.sovity.edc.utils.jsonld.vocab.Prop;
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
@Schema(description = "One field to sort by", requiredMode = Schema.RequiredMode.NOT_REQUIRED, deprecated = true)
public class CatalogSortBy {
    @Schema(description = "Asset property path", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"" + Prop.Edc.ASSET_ID + "\"]")
    private List<String> assetPropertyPath;

    @Schema(description = "Direction to sort by", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "ASC")
    private CatalogSortByDirection direction;
}
