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

import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import transfer.broker.IdsBrokerService;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SynchronizerImpl implements Synchronizer {

    private final IdsBrokerService idsBrokerService;
    private final Monitor monitor;
    private final BrokerDefinitionProvider contractDefinitionProvider;
    private final AssetIndex assetIndex;

    public SynchronizerImpl(
            IdsBrokerService idsBrokerService,
            Monitor monitor,
            BrokerDefinitionProvider contractOfferProvider,
            AssetIndex assetIndex) {
        this.idsBrokerService = idsBrokerService;
        this.monitor = monitor;
        this.contractDefinitionProvider = contractOfferProvider;
        this.assetIndex = assetIndex;
    }

    @Override
    public void synchronize(URL brokerBaseUrl) {
        //Assets -> IDS-Broker
        synchronizeBroker(brokerBaseUrl);
    }

    private void synchronizeBroker(URL brokerBaseUrl) {
        synchronizeDeletedOffers(brokerBaseUrl);
        synchronizeCreatedOffers(brokerBaseUrl);
    }

    private void synchronizeCreatedOffers(URL brokerBaseUrl) {
        var createdContractDefinitions = contractDefinitionProvider.pollCreatedContractDefinitions();
        var resourceMap = new HashMap<String, Asset>();
        for (var contractDefinition : createdContractDefinitions) {
            var assetsFromContractDefinition = getAssetsFromContractDefinition(contractDefinition);
            for (var asset : assetsFromContractDefinition) {
                var resourceId = String.format("%s-%s",
                        contractDefinition.getId(),
                        asset.getId());
                resourceMap.put(resourceId, asset);
            }
        }

        addFederatedCatalogResourcesToBroker(resourceMap, brokerBaseUrl);
    }

    private void synchronizeDeletedOffers(URL brokerBaseUrl) {
        var deletedContractDefinitionIds = contractDefinitionProvider.pollDeletedContractDefinitionIds();
        var deletedBrokerIds = getDeletedBrokerIds(brokerBaseUrl, deletedContractDefinitionIds);

        removeResourcesFromBroker(deletedBrokerIds, brokerBaseUrl);
    }

    private List<Asset> getAssetsFromContractDefinition(ContractDefinition contractDefinition) {
        var querySpec = QuerySpec.Builder.newInstance()
                .filter(new ArrayList<>(contractDefinition.getSelectorExpression().getCriteria()))
                .build();
        return assetIndex.queryAssets(querySpec).toList();
    }

    private List<String> getDeletedBrokerIds(URL brokerBaseUrl, List<String> deletedContractDefinitionIds) {
        var deletedResourceIds = new ArrayList<String>();
        for (var contractDefinitionId : deletedContractDefinitionIds) {
            var resourceIdsByQuery = idsBrokerService.findResourceIdsByQuery(brokerBaseUrl, contractDefinitionId);
            deletedResourceIds.addAll(resourceIdsByQuery);
        }
        return deletedResourceIds;
    }

    private void addFederatedCatalogResourcesToBroker(
            Map<String, Asset> federatedCatalogResourceMap,
            URL brokerBaseUrl) {
        var createdResourceSet = new HashSet<>(federatedCatalogResourceMap.keySet());

        for (var createdResourceId : createdResourceSet) {
            monitor.info(String.format("Registering resource %s at broker", createdResourceId));
            idsBrokerService.registerResourceAtBroker(
                    brokerBaseUrl,
                    createdResourceId,
                    federatedCatalogResourceMap.get(createdResourceId));
        }
    }

    private void removeResourcesFromBroker(
            List<String> deletedBrokerResourceIds,
            URL brokerBaseUrl) {
        for (var deletedResourceId : deletedBrokerResourceIds) {
            monitor.info(String.format("Removing resource %s from broker", deletedResourceId));
            idsBrokerService.unregisterResourceAtBroker(
                    brokerBaseUrl,
                    deletedResourceId);
        }
    }
}
