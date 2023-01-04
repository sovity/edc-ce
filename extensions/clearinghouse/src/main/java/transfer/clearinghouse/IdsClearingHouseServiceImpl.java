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
package transfer.clearinghouse;

import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.protocol.ids.service.ConnectorServiceSettings;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.system.Hostname;

import java.net.URL;

public class IdsClearingHouseServiceImpl implements IdsClearingHouseService {

    private static final String CONTEXT_BROKER_REGISTRATION = "BrokerRegistration";

    private final RemoteMessageDispatcherRegistry dispatcherRegistry;
    private final ConnectorServiceSettings connectorServiceSettings;
    private final Hostname hostname;

    public IdsClearingHouseServiceImpl(
            RemoteMessageDispatcherRegistry dispatcherRegistry,
            ConnectorServiceSettings connectorServiceSettings,
            Hostname hostname) {
        this.dispatcherRegistry = dispatcherRegistry;
        this.connectorServiceSettings = connectorServiceSettings;
        this.hostname = hostname;
    }

    @Override
    public void logContractAgreement(ContractAgreement contractAgreement, URL clearingHouseLogUrl) {
        //TODO: log contractAgreement to CH (clearingHouseLogUrl)
    }
}
