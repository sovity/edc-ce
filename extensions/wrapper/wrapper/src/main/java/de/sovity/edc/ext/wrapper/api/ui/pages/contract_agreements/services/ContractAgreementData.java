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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ext.db.jooq.tables.records.SovityContractTerminationRecord;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;

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
        List<TransferProcess> transfers,
        SovityContractTerminationRecord termination
) {

}
