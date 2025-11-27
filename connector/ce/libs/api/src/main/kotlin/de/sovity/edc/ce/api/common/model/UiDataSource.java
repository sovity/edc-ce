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
@Schema(description = "Type-safe data source as supported by the sovity product landscape. Contains extension points for using custom data address properties.")
public class UiDataSource {
    @Schema(
        description = "Data Address Type.",
        defaultValue = "CUSTOM",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private DataSourceType type;

    @Schema(
        description = "Only for type HTTP_DATA",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceHttpData httpData;

    @Schema(
        description = "Only for type ON_REQUEST",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceOnRequest onRequest;

    @Schema(
        description = "Only for type AZURE_STORAGE",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceAzureStorage azureStorage;

    @Schema(
        description = "For all types. Custom Data Address Properties.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, String> customProperties;
}
