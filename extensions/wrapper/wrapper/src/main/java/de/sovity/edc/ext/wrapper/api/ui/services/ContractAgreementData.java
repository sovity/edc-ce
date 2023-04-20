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

package de.sovity.edc.ext.wrapper.api.ui.services;

import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.List;


/**
 * Data for a contract agreement as required by the contract agreement page.
 *
 * @param agreement   contract agreement
 * @param negotiation contract negotiation
 * @param asset       asset
 * @param transfers   transfer processes
 */
public record ContractAgreementData(
        ContractAgreement agreement,
        ContractNegotiation negotiation,
        Asset asset,
        List<TransferProcess> transfers
) {

}
