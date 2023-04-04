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
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "EDC-status-defining Contract-Agreements")
public class ContractAgreementDto {
    @Schema(description = "EDC Contract-Agreements")
    private ContractAgreement contractAgreement;

    @Schema(description = "EDC Contract-Agreement asset")
    private Asset asset;

    @Schema(description = "EDC Contract-Agreement policy")
    private Policy policy;

    @Schema(description = "EDC Contract-Agreement contract negotiation")
    private ContractNegotiation contractNegotiation;

    @Schema(description = "EDC Contract-Agreement transfer processes")
    private List<TransferProcess> transferProcesses;
}
