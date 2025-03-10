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

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A value either inlined or to be fetched from the Vault. " +
    "Raw secret values will land in the database and will be retrievable via the " +
    "Eclipse EDC Management API.")
public class SecretValue {
    @Schema(
        description = "Secret Name / Vault Key Name.",
        example = "myApiAuthKey",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String secretName;

    @Schema(
        description = "Raw inline Value.",
        example = "<auth key here>",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String rawValue;
}
