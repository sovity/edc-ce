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
package transfer.transfer;

import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.spi.monitor.Monitor;
import transfer.clearinghouse.IdsClearingHouseService;

import java.net.URL;

public class SynchronizerImpl implements Synchronizer {

    private final IdsClearingHouseService idsClearinghouseService;
    private final Monitor monitor;
    private final ClearingHouseDefinitionProvider provider;
    public SynchronizerImpl(
            IdsClearingHouseService idsClearinghouseService,
            Monitor monitor,
            ClearingHouseDefinitionProvider provider) {
        this.idsClearinghouseService = idsClearinghouseService;
        this.monitor = monitor;
        this.provider = provider;
    }

    @Override
    public void synchronize(URL clearingHouseLogUrl) {
        //ContractAgreements -> IDS-ClearingHouse
        synchronizeClearingHouse(clearingHouseLogUrl);
    }

    private void synchronizeClearingHouse(URL clearingHouseLogUrl) {
        var contractAgreements =  provider.pollContractAgreements();
        for (var contractAgreement : contractAgreements) {
            logContractAgreement(contractAgreement, clearingHouseLogUrl);
        }
    }

    private void logContractAgreement(ContractAgreement contractAgreement,
                                      URL clearingHouseLogUrl) {
        monitor.info(String.format("Logging contract agreement %s at clearing house", contractAgreement.getId()));
        idsClearinghouseService.logContractAgreement(contractAgreement, clearingHouseLogUrl);
    }
}
