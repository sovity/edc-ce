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

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Callback address to be called after a transfer or negotiation success/failure")
public class CallbackAddressDto {
    @Schema(description = "URL to be called when the event is fired", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(
        description = "Event Types to subscribe to. ",
        defaultValue = "TRANSFER_PROCESS_COMPLETED",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CallbackAddressEventType> events;

    @Schema(description = "Authentication: Header Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authHeaderName;

    @Schema(description = "Authentication: Header Value (Vault Secret Name)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authHeaderVaultSecretName;
}
