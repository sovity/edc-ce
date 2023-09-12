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
 *       sovity GmbH - initial API and implementation
 *
 */
package de.sovity.edc.client;


import de.sovity.edc.client.gen.model.AssetCreateRequest;
import de.sovity.edc.ext.wrapper.utils.EdcPropertyUtils;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.SimpleDateFormat;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.spi.types.domain.DataAddress.EDC_DATA_ADDRESS_TYPE_PROPERTY;

@ApiTest
@ExtendWith(EdcExtension.class)
public class AssetApiServiceTest {

    public static final String DATA_SINK = "http://my-data-sink/api/stuff";
    public static final String DATA_ADDRESS_TYPE = "HttpData";
    EdcPropertyUtils edcPropertyUtils;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        edcPropertyUtils = new EdcPropertyUtils();
    }

    @Test
    void assetPage(AssetService assetStore) {
        // arrange
        var client = TestUtils.edcClient();
        var privateProperties = Map.of("random-private-prop", "456");
        var properties = Map.of(
                Asset.PROPERTY_ID, "asset-1",
                "random-prop", "123"
        );
        createAsset(assetStore, "2023-06-01", properties, privateProperties);

        // act
        var result = client.uiApi().assetPage();

        // assert
        var assets = result.getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAdditionalProperties()).isEqualTo(properties);
        assertThat(asset.getPrivateProperties()).isEqualTo(privateProperties);
    }


    @Test
    void assetPageSorting(AssetService assetService) {
        // arrange
        var client = TestUtils.edcClient();
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-1"), Map.of());
        createAsset(assetService, "2023-06-03", Map.of(Asset.PROPERTY_ID, "asset-3"), Map.of());
        createAsset(assetService, "2023-06-02", Map.of(Asset.PROPERTY_ID, "asset-2"), Map.of());

        // act
        var result = client.uiApi().assetPage();

        // assert
        assertThat(result.getAssets())
                .extracting(asset -> asset.getAdditionalProperties().get(Asset.PROPERTY_ID))
                .containsExactly("asset-3", "asset-2", "asset-1");
    }

    @Test
    void testAssetCreation(AssetService assetService) {
        // arrange
        var client = TestUtils.edcClient();
        var properties = Map.of(
                Asset.PROPERTY_ID, "asset-1",
                "random-prop", "123"
        );
        var privateProperties = Map.of("random-private-prop", "456");
        var dataAddressProperties = Map.of(
                EDC_DATA_ADDRESS_TYPE_PROPERTY, DATA_ADDRESS_TYPE,
                "baseUrl", DATA_SINK
        );
        var assetRequest = AssetCreateRequest.builder()
                .properties(properties)
                .privateProperties(privateProperties)
                .dataAddressProperties(dataAddressProperties)
                .build();

        // act
        var response = client.uiApi().createAsset(assetRequest);

        // assert
        assertThat(response.getId()).isEqualTo(properties.get(Asset.PROPERTY_ID));
        var assets = assetService.query(QuerySpec.max()).getContent().toList();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getProperties()).isEqualTo(properties);
        assertThat(asset.getPrivateProperties()).isEqualTo(privateProperties);
        assertThat(asset.getDataAddress().getProperties()).isEqualTo(dataAddressProperties);
    }

    @Test
    void testDeleteAsset(AssetService assetService) {
        // arrange
        var client = TestUtils.edcClient();
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-1"), Map.of());
        assertThat(assetService.query(QuerySpec.max()).getContent()).isNotEmpty();

        // act
        var response = client.uiApi().deleteAsset("asset-1");

        // assert
        assertThat(response.getId()).isEqualTo("asset-1");
        assertThat(assetService.query(QuerySpec.max()).getContent()).isEmpty();
    }

    private void createAsset(
            AssetService assetService,
            String date,
            Map<String, String> properties,
            Map<String, String> privateProperties
    ) {

        DataAddress dataAddress = DataAddress.Builder.newInstance()
                .type(DATA_ADDRESS_TYPE)
                .property("baseUrl", DATA_SINK)
                .build();

        var asset = Asset.Builder.newInstance()
                .id(properties.get(Asset.PROPERTY_ID))
                .properties(edcPropertyUtils.toMapOfObject(properties))
                .dataAddress(dataAddress)
                .privateProperties(edcPropertyUtils.toMapOfObject(privateProperties))
                .createdAt(dateFormatterToLong(date))
                .build();
        assetService.create(asset);
    }

    @SneakyThrows
    private static long dateFormatterToLong(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(date).getTime();
    }

}

