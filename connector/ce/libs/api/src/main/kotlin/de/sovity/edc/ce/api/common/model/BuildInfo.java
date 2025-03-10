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

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Build information about the connector backend.")
public class BuildInfo {
    @Schema(
        description = "Container build date. May be overridden.",
        example = "2021-02-03T04:05:06.789Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String buildDate;

    @Schema(
        description = "Container build release version or a combination of last tag + commit hash. May be overridden.",
        example = "v4.5.1-1445-gff95cb8e2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String buildVersion;
}
