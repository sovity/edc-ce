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

package de.sovity.edc.ext.wrapper.api.usecase.model;

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
@Schema(description = "EDC-status-defining KPIs")
public class KpiResult {
    @Schema(description = "Counts of assets", required = true)
    private Integer assetsCount;

    @Schema(description = "Counts of policies", required = true)
    private Integer policiesCount;

    @Schema(description = "Counts of contract definitions", required = true)
    private Integer contractDefinitionsCount;

    @Schema(description = "Counts of transfer processes", required = true)
    private Integer transferProcessesCount;
}
