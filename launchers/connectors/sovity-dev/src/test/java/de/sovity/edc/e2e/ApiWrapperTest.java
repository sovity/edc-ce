/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractAgreementTransferRequest;
import de.sovity.edc.client.gen.model.ContractAgreementTransferRequestParams;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.eclipse.edc.spi.types.domain.DataAddress.EDC_DATA_ADDRESS_TYPE_PROPERTY;

class ApiWrapperTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    public static final String DATA_SINK = "http://my-data-sink/api/stuff";
    public static final String DATA_ADDRESS_TYPE = "HttpData";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase(1);
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase(2);

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient consumerClient;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 22000, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void testAssetCreation() {
        // arrange
        var assetId = UUID.randomUUID().toString();


        Map<String, Object> dataSource = Map.of(
                "name", "transfer-test",
                "baseUrl",  dataAddress.getDataSourceUrl("dummy test data"),
                "type", "HttpData",
                "proxyQueryParams", "true"
        );
        providerConnector.createAsset(assetId, dataSource);

        var assets = consumerClient.uiApi().assetPage();

       assertThat(assets.getAssets()).hasSize(1);
        var asset = assets.getAssets().get(0);
        assertThat(asset.getName()).isEqualTo("AssetName");
        assertThat(asset.getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
    }
    // TODO test policy conversion
    // TODO test asset creation, make this test a full circle in data offering and consumption via the UI API
}
