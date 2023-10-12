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
import io.restassured.path.json.JsonPath;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.asset.AssetSelectorExpression;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
            var connectorEndpoint = TestUtils.PROTOCOL_ENDPOINT;
            var connectorUpdater = BrokerServerExtensionContext.instance.connectorUpdater();
            var connectorCreator = BrokerServerExtensionContext.instance.connectorCreator();

            createAlwaysTruePolicyDefinition(policyDefinitionStore);
            createAlwaysTrueContractDefinition(contractDefinitionStore);
            
            var nestedObjProperty = new LinkedHashMap<String, Object>(Map.of(
                    "test-string", "hello",
                    "test-uri", "https://w3id.org/idsa/code/AB",
                    "http://test-uri-key", "value",

                    "test-array", new ArrayList<>(List.of("a", "b")),
                    "test-float", 5.1,
                    "test-int", 5,
                    "test-boolean", true,

                    "test-obj", new LinkedHashMap<>(Map.of("key", "value"))
            ));
            nestedObjProperty.put("test-null", null);

            var asset = Asset.Builder.newInstance()
                    .id("test-asset-1")
                    .property(AssetProperty.ASSET_ID, "test-asset-1")
                    .property(AssetProperty.ASSET_NAME, "Test Asset 1")
                    .property("test-string", "hello")
                    .property("test-uri", "https://w3id.org/idsa/code/AB")
                    .property("http://test-uri-key", "value")

                    .property("test-array", new ArrayList<>(List.of("a", "b")))
                    .property("test-float", 5.1)
                    .property("test-int", 5)
                    .property("test-boolean", true)

                    .property("test-obj", nestedObjProperty)
                    .build();

            assetService.create(asset, dataAddress());
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

            var props = JsonPath.from(dataOffer.getAssetProperties().data());
            assertThat(props.getString("\"asset:prop:name\"")).isEqualTo("Test Asset 1");
            assertThat(props.getString("test-string")).isEqualTo("hello");
            assertThat(props.getString("test-uri")).isEqualTo("https://w3id.org/idsa/code/AB");
            assertThat(props.getString("http://test-uri-key")).isEqualTo("value");

            assertThat(props.getString("test-array")).isEqualTo("[\"a\",\"b\"]");
            assertThat(props.getString("test-int")).isEqualTo("5");
            assertThat(props.getString("test-float")).isEqualTo("5.1");
            assertThat(props.getString("test-boolean")).isEqualTo("true");

            var testObj = JsonPath.from(props.getString("test-obj"));
            assertThat((String) testObj.get("test-string")).isEqualTo("hello");
            assertThat((String) testObj.get("test-uri")).isEqualTo("https://w3id.org/idsa/code/AB");
            assertThat((String) testObj.get("http://test-uri-key")).isEqualTo("value");

            assertThat((List<?>) testObj.get("test-array")).isEqualTo(List.of("a", "b"));
            assertThat((Integer) testObj.get("test-int")).isEqualTo(5);
            assertThat((Float) testObj.get("test-float")).isEqualTo(5.1f);
            assertThat((Boolean) testObj.get("test-boolean")).isTrue();
            assertThat((Map<?, ?>) testObj.get("test-obj")).isEqualTo(Map.of("key", "value"));

            // the nested object's null will have disappeared
            assertThat(testObj.getMap("")).containsKey("test-string");
            assertThat(testObj.getMap("")).doesNotContainKey("test-null");

            var contractOffers = dsl.selectFrom(Tables.DATA_OFFER_CONTRACT_OFFER).stream().toList();
            assertThat(contractOffers).hasSize(1);
        });
    }

    @Test
    void testTopLevelAssetPropertyCannotBeNull(AssetService assetService) {
        var asset = Asset.Builder.newInstance()
                .id("test-asset-1")
                .property("test-null", null)
                .build();
        var dataAddress = dataAddress();
        assertThatThrownBy(() -> assetService.create(asset, dataAddress))
                .isInstanceOf(EdcPersistenceException.class)
                .hasMessage("java.lang.NullPointerException: Cannot invoke \"Object.getClass()\" because the return value of \"java.util.Map$Entry.getValue()\" is null");
    }

    private DataAddress dataAddress() {
        return DataAddress.Builder.newInstance()
                .properties(Map.of(
                        "type", "HttpData",
                        "baseUrl", "https://jsonplaceholder.typicode.com/todos/1"
                ))
                .build();
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

}
