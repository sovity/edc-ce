/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ce.db.jooq.tables.records.SovityContractTerminationRecord;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;

import java.util.List;


/**
 * Data for a contract agreement as required by the contract agreement page.
 *
 * @param agreement contract agreement
 * @param negotiation contract negotiation
 * @param asset asset
 * @param transfers transfer processes
 */
public record ContractAgreementData(
    ContractAgreement agreement,
    ContractNegotiation negotiation,
    Asset asset,
    List<TransferProcess> transfers,
    SovityContractTerminationRecord termination
) {

}
