/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.refreshing;

import de.sovity.edc.ext.brokerserver.BrokerServerExtensionContext;
import de.sovity.edc.ext.brokerserver.TestUtils;
import de.sovity.edc.ext.brokerserver.dao.AssetProperty;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.asset.AssetSelectorExpression;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ConnectorUpdaterTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
    }

    @Test
    void testConnectorUpdate(
            AssetService assetService,
            PolicyDefinitionStore policyDefinitionStore,
            ContractDefinitionStore contractDefinitionStore
    ) {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var connectorUpdater = BrokerServerExtensionContext.instance.connectorUpdater();
            var connectorCreator = BrokerServerExtensionContext.instance.connectorCreator();
            String connectorEndpoint = TestUtils.PROTOCOL_ENDPOINT;

            createAlwaysTruePolicyDefinition(policyDefinitionStore);
            createAlwaysTrueContractDefinition(contractDefinitionStore);
            createAsset(assetService, "test-asset-1", "Test Asset 1");
            connectorCreator.addConnector(dsl, connectorEndpoint);

            // act
            connectorUpdater.updateConnector(connectorEndpoint);

            // assert
            var connectors = dsl.selectFrom(Tables.CONNECTOR).stream().toList();
            assertThat(connectors.get(0).getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.ONLINE);
            assertThat(connectors.get(0).getEndpoint()).isEqualTo(connectorEndpoint);

            var dataOffers = dsl.selectFrom(Tables.DATA_OFFER).stream().toList();
            assertThat(dataOffers).hasSize(1);

            var dataOffer = dataOffers.get(0);
            assertThat(dataOffer.getAssetId()).isEqualTo("test-asset-1");
            assertThat(dataOffer.getAssetProperties().data()).contains("Test Asset 1");

            var contractOffers = dsl.selectFrom(Tables.DATA_OFFER_CONTRACT_OFFER).stream().toList();
            assertThat(contractOffers).hasSize(1);
        });
    }

    private void createAlwaysTruePolicyDefinition(PolicyDefinitionStore policyDefinitionStore) {
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .id("always-true")
                .policy(Policy.Builder.newInstance().build())
                .build();
        policyDefinitionStore.save(policyDefinition);
    }

    public void createAlwaysTrueContractDefinition(ContractDefinitionStore contractDefinitionStore) {
        var contractDefinition = ContractDefinition.Builder.newInstance()
                .id("always-true-cd")
                .contractPolicyId("always-true")
                .accessPolicyId("always-true")
                .selectorExpression(AssetSelectorExpression.SELECT_ALL)
                .validity(1000) //else throws "validity must be strictly positive"
                .build();
        contractDefinitionStore.save(contractDefinition);
    }

    private void createAsset(
            AssetService assetService,
            String assetId,
            String assetName
    ) {
        var asset = Asset.Builder.newInstance()
                .id(assetId)
                .property(AssetProperty.ASSET_ID, assetId)
                .property(AssetProperty.ASSET_NAME, assetName)
                .build();
        var dataAddress = DataAddress.Builder.newInstance()
                .properties(Map.of(
                "type", "HttpData",
                "baseUrl", "https://jsonplaceholder.typicode.com/todos/1"
                ))
                .build();
        assetService.create(asset, dataAddress);
    }
}
