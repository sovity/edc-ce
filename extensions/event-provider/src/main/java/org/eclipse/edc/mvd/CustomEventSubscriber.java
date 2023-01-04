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
package org.eclipse.edc.mvd;

import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.event.contractdefinition.ContractDefinitionCreated;
import org.eclipse.edc.spi.event.contractdefinition.ContractDefinitionDeleted;
import org.eclipse.edc.spi.event.contractnegotiation.ContractNegotiationConfirmed;
import transfer.transfer.BrokerDefinitionProvider;
import transfer.transfer.ClearingHouseDefinitionProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomEventSubscriber implements EventSubscriber,
        BrokerDefinitionProvider, ClearingHouseDefinitionProvider {
    private final ContractDefinitionStore contractDefinitionStore;

    private final ContractNegotiationStore contractNegotiationStore;
    private final List<ContractDefinition> createdContractDefinitions;
    private final List<String> deletedContractDefinitionIds;

    private final List<ContractAgreement> contractAgreements;

    public CustomEventSubscriber(ContractDefinitionStore contractDefinitionStore,
                                 ContractNegotiationStore contractNegotiationStore) {
        this.contractDefinitionStore = contractDefinitionStore;
        this.contractNegotiationStore = contractNegotiationStore;
        createdContractDefinitions = new LinkedList<>();
        deletedContractDefinitionIds = new LinkedList<>();
        contractAgreements = new LinkedList<>();
    }

    public void on(Event event) {
        if (event instanceof ContractDefinitionCreated contractDefinitionCreated) {
            //ContractDefinitionCreated -> Send EDC-Asset as IDS-Resource to IDS-Broker
            handleContractDefinitionCreated(contractDefinitionCreated);
        } else if (event instanceof ContractDefinitionDeleted contractDefinitionDeleted) {
            //ContractDefinitionDeleted -> Remove EDC-Asset as IDS-Resource from IDS-Broker
            handleContractDefinitionDeleted(contractDefinitionDeleted);
        } else if (event instanceof ContractNegotiationConfirmed contractNegotiationConfirmed) {
            //ContractNegotiationConfirmed -> Log to ClearingHouse
            handleContractNegotiationConfirmed(contractNegotiationConfirmed);
        }
    }

    @Override
    public List<ContractDefinition> pollCreatedContractDefinitions() {
        var createdContractDefinitionsCopy = new ArrayList<>(createdContractDefinitions);
        createdContractDefinitions.clear();
        return createdContractDefinitionsCopy;
    }

    @Override
    public List<String> pollDeletedContractDefinitionIds() {
        var deletedAssetsCopy = new ArrayList<>(deletedContractDefinitionIds);
        deletedContractDefinitionIds.clear();
        return deletedAssetsCopy;
    }

    @Override
    public List<ContractAgreement> pollContractAgreements() {
        var contractAgreementsCopy = new ArrayList<>(contractAgreements);
        contractAgreements.clear();
        return contractAgreementsCopy;
    }

    private void handleContractNegotiationConfirmed(ContractNegotiationConfirmed contractNegotiationConfirmed) {
        var eventPayload = contractNegotiationConfirmed.getPayload();
        var contractNegotiationId = eventPayload.getContractNegotiationId();
        var contractNegotiation = contractNegotiationStore.find(contractNegotiationId);
        var contractAgreement = contractNegotiation.getContractAgreement();
        this.contractAgreements.add(contractAgreement);
    }

    private void handleContractDefinitionDeleted(ContractDefinitionDeleted contractDefinitionDeleted) {
        var eventPayload = contractDefinitionDeleted.getPayload();
        var contractDefinitionId = eventPayload.getContractDefinitionId();
        this.deletedContractDefinitionIds.add(contractDefinitionId);
    }

    private void handleContractDefinitionCreated(ContractDefinitionCreated contractDefinitionCreated) {
        var eventPayload = contractDefinitionCreated.getPayload();
        var contractDefinitionId = eventPayload.getContractDefinitionId();
        var contractDefinition = contractDefinitionStore.findById(contractDefinitionId);
        this.createdContractDefinitions.add(contractDefinition);
    }
}
