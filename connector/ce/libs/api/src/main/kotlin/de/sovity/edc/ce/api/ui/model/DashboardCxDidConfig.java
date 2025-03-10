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
package de.sovity.edc.ce.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "Managed Identity Wallet (MIW) Config")
public class DashboardCxDidConfig {
    @Schema(description = "My DID / edc.iam.issuer.id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String myDid;

    @Schema(description = "Wallet Token Url / edc.iam.sts.oauth.token.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String walletTokenUrl;

    @Schema(description = "Trusted VC Issuer / edc.iam.trusted-issuer.cofinity.id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trustedVcIssuer;

    @Schema(description = "BDRS Url / tx.iam.iatp.bdrs.server.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bdrsUrl;

    @Schema(description = "STS DIM Url / edc.iam.sts.dim.url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dimUrl;
}
