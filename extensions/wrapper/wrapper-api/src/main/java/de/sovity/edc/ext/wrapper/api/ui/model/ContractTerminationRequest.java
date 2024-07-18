/*
 *  Copyright (c) 2024 sovity GmbH
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
import jakarta.validation.constraints.Size;
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
@Schema(description = "Data for terminating a Contract Agreement")
public class ContractTerminationRequest {
    public static final int MAX_REASON_SIZE = 100;
    public static final int MAX_DETAIL_SIZE = 1000;

    @Schema(
        title = "Termination detail",
        description = "A user explanation to detail why the contract was terminated.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = MAX_DETAIL_SIZE)
    String detail;

    @Schema(
        title = "Termination reason",
        description = "A short reason why this contract was terminated",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = MAX_REASON_SIZE)
    String reason;
}
