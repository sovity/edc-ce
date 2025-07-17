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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "OAuth 2 authentication information"
)
public class UiHttpAuthOauth2 {

    @Schema(
        description = "The token URL where the access-token can be fetched from.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private String tokenUrl;

    @Schema(
        description = "(optional) The requested scope.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    private String scope;

    @Schema(
        description = "The type of credential.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UiHttpOauth2AuthType type;

    @Schema(
        description = "Required if type=PRIVATE_KEY. For private key-based authorization.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiHttpOauth2PrivateKeyAuthorization privateKey;

    @Schema(
        description = "Required if type=SHARED_SECRET. For shared secret-based authorization.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiHttpOauth2SharedSecretAuthorization sharedSecret;
}
