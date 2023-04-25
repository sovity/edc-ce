/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Transfer Process State interpreted")
public class TransferProcessState {
    @Schema(description = "State name or 'CUSTOM'. State names only exist for original EDC Transfer Process States.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "State code", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer code;
    @Schema(description = "Whether we are running, in an error state or done.", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessSimplifiedState simplifiedState;
}
