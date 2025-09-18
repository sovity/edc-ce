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
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "OAuth 2 authorization with a private key"
)
public class UiHttpOauth2PrivateKeyAuthorization {
    @Schema(
        description = "The vault entry name of the private key used to sign the JWT sent to the Oauth2 server.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String privateKeyName;

    @Schema(
        description = "The validity of the JWT token sent to the Oauth2 server (in seconds).",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long tokenValidityInSeconds;
}
