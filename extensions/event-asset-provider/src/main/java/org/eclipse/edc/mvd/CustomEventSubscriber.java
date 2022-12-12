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

import catalogtransfer.transfer.ContractDefinitionProvider;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.event.contractdefinition.ContractDefinitionCreated;
import org.eclipse.edc.spi.event.contractdefinition.ContractDefinitionDeleted;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomEventSubscriber implements EventSubscriber, ContractDefinitionProvider {
    private final ContractDefinitionStore contractDefinitionStore;

    private final AssetIndex assetIndex;

    private final List<ContractDefinition> createdContractDefinitions;
    private final List<String> deletedContractDefinitionIds;

    public CustomEventSubscriber(ContractDefinitionStore contractDefinitionStore,
                                 AssetIndex assetIndex) {
        this.contractDefinitionStore = contractDefinitionStore;
        this.assetIndex = assetIndex;
        createdContractDefinitions = new LinkedList<>();
        deletedContractDefinitionIds = new LinkedList<>();
    }

    public void on(Event event) {
        if (event instanceof ContractDefinitionCreated contractDefinitionCreated) {
            var eventPayload = contractDefinitionCreated.getPayload();
            var contractDefinitionId = eventPayload.getContractDefinitionId();
            var contractDefinition = contractDefinitionStore.findById(contractDefinitionId);
            this.createdContractDefinitions.add(contractDefinition);
        } else if (event instanceof ContractDefinitionDeleted contractDefinitionDeleted) {
            var eventPayload = contractDefinitionDeleted.getPayload();
            var contractDefinitionId = eventPayload.getContractDefinitionId();
            this.deletedContractDefinitionIds.add(contractDefinitionId);
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
}
