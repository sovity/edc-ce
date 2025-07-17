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
    description = "OAuth 2 shared secret authorization. This uses the OAuth2 client credentials flow."
)
public class UiHttpOauth2SharedSecretAuthorization {

    @Schema(
        description = "The client identifier.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clientId;


    @Schema(
        description = "The key with which the shared secret for authenticating to the Oauth2 server is stored into the Vault.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clientSecretName;
}
