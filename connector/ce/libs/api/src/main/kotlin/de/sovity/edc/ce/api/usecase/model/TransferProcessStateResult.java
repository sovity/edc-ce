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
package de.sovity.edc.ce.api.usecase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sovity.edc.ce.api.ui.model.TransferProcessState;
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
@Schema(description = "Transfer process state")
public class TransferProcessStateResult {
    @Schema(description = "Transfer Process ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transferId;

    @Schema(description = "Transfer Process State", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessState state;

    @Schema(description = "Transfer Process State Last Change", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime stateChangedAt;

    @Schema(description = "Whether this transfer is available for consumption via an EDR.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isEdrConsumable;
}
