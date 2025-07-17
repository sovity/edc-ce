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
@Schema(
    description = "Basic Auth as defined in RFC 7617. This is a wrapper for setting the header as Authorization: Basic Zm9vOnBhc3N3b3Jk. " +
        "Using basic auth is not recommended in general for security reasons, but it is also not recommended because the secret will be " +
        "saved in the database rather than the vault."
)
public class UiHttpAuthBasic {

    @Schema(
        description = "The username",
        example = "foo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @Schema(
        description = "The password",
        example = "password",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
