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
package de.sovity.extension.clearinghouse.service;

import de.sovity.extension.clearinghouse.sender.message.LogMessage;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.protocol.ids.service.ConnectorServiceSettings;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.event.contractnegotiation.ContractNegotiationConfirmed;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.Hostname;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class IdsClearingHouseServiceImpl implements IdsClearingHouseService, EventSubscriber {

    private static final String CONTEXT_CLEARINGHOUSE = "ClearingHouse";

    private final RemoteMessageDispatcherRegistry dispatcherRegistry;
    private final ConnectorServiceSettings connectorServiceSettings;
    private final Hostname hostname;
    private final URL clearingHouseLogUrl;
    private final ContractNegotiationStore contractNegotiationStore;
    private final Monitor monitor;

    public IdsClearingHouseServiceImpl(
            RemoteMessageDispatcherRegistry dispatcherRegistry,
            ConnectorServiceSettings connectorServiceSettings,
            Hostname hostname,
            URL clearingHouseLogUrl,
            ContractNegotiationStore contractNegotiationStore,
            Monitor monitor) {
        this.dispatcherRegistry = dispatcherRegistry;
        this.connectorServiceSettings = connectorServiceSettings;
        this.hostname = hostname;
        this.clearingHouseLogUrl = clearingHouseLogUrl;
        this.contractNegotiationStore = contractNegotiationStore;
        this.monitor = monitor;
    }

    @Override
    public void logContractAgreement(ContractAgreement contractAgreement, URL clearingHouseLogUrl) {
        monitor.info("Logging contract agreement to ClearingHouse");
        try {
            var connectorBaseUrl = new URI(String.format("http://%s/", hostname.get()));
            var logMessage = new LogMessage(clearingHouseLogUrl, connectorBaseUrl, contractAgreement);

            dispatcherRegistry.send(Object.class, logMessage, () -> CONTEXT_CLEARINGHOUSE);
        } catch (URISyntaxException e) {
            throw new EdcException("Could not create connectorBaseUrl. Hostname can be set using:" +
                    " edc.hostname", e);
        }
    }

    @Override
    public void on(Event<?> event) {
        if (event instanceof ContractNegotiationConfirmed contractNegotiationConfirmed) {
            var contractAgreement = resolveContractAgreement(contractNegotiationConfirmed);
            logContractAgreement(contractAgreement, clearingHouseLogUrl);
        }
    }

    private ContractAgreement resolveContractAgreement(ContractNegotiationConfirmed contractNegotiationConfirmed) {
        var eventPayload = contractNegotiationConfirmed.getPayload();
        var contractNegotiationId = eventPayload.getContractNegotiationId();
        var contractNegotiation = contractNegotiationStore.find(contractNegotiationId);
        return contractNegotiation.getContractAgreement();
    }
}
