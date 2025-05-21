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

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Issued for a single transfer, calling the given endpoint allows you to directly interact with the data source." +
    " Depending on the asset you can pass data to the data source, provide query params or append a custom sub-path.")
public class EdrDto {
    @Schema(description = "Base URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String baseUrl;

    @Schema(description = "Value for the 'Authorization' header.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorizationHeaderValue;

    @Schema(description = "Expiration date of the EDR token or null, if EDR never expires. After expiration you must re-fetch the EDR.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime expiresAt;
}
