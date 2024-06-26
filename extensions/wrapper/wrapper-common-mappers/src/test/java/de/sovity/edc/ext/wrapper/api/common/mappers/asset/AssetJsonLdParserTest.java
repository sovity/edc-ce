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

package de.sovity.edc.ext.wrapper.api.common.mappers.asset;

import de.sovity.edc.ext.wrapper.api.common.mappers.Factory;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceAvailability;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AssetJsonLdParserTest {
    AssetJsonLdBuilder assetJsonLdBuilder;
    AssetJsonLdParser assetJsonLdParser;

    private static final String ASSET_ID = "asset-id";
    private static final String ORG_NAME = "org-name";
    private static final String ENDPOINT = "endpoint";
    private static final String PARTICPANT_ID = "participant-id";

    @BeforeEach
    void setup() {
        var ownConnectorEndpointService = mock(OwnConnectorEndpointService.class);
        assetJsonLdBuilder = Factory.newAssetJsonLdBuilder(ownConnectorEndpointService);
        assetJsonLdParser = Factory.newAssetJsonLdParser(ownConnectorEndpointService);
    }

    @Test
    void test_create_httpData_methodParameterization() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .enableMethodParameterization(true)
                .build())
            .build();

        var createRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expected = dummyUiAsset()
            .dataSourceAvailability(DataSourceAvailability.LIVE)
            .httpDatasourceHintsProxyMethod(true)
            .build();


        // act
        var jsonLd = assetJsonLdBuilder.createAssetJsonLd(createRequest, ORG_NAME);
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_create_httpData_pathParameterization() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .enablePathParameterization(true)
                .build())
            .build();

        var createRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expected = dummyUiAsset()
            .dataSourceAvailability(DataSourceAvailability.LIVE)
            .httpDatasourceHintsProxyPath(true)
            .build();

        // act
        var jsonLd = assetJsonLdBuilder.createAssetJsonLd(createRequest, ORG_NAME);
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_create_httpData_queryParameterization() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .enableQueryParameterization(true)
                .build())
            .build();

        var createRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expected = dummyUiAsset()
            .dataSourceAvailability(DataSourceAvailability.LIVE)
            .httpDatasourceHintsProxyQueryParams(true)
            .build();

        // act
        var jsonLd = assetJsonLdBuilder.createAssetJsonLd(createRequest, ORG_NAME);
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_create_httpData_bodyParameterization() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .enableBodyParameterization(true)
                .build())
            .build();

        var createRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expected = dummyUiAsset()
            .dataSourceAvailability(DataSourceAvailability.LIVE)
            .httpDatasourceHintsProxyBody(true)
            .build();

        // act
        var jsonLd = assetJsonLdBuilder.createAssetJsonLd(createRequest, ORG_NAME);
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_create_onRequest() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.ON_REQUEST)
            .onRequest(UiDataSourceOnRequest.builder()
                .contactEmail("contact@example.com")
                .contactPreferredEmailSubject("Test")
                .build())
            .build();

        var createRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expected = dummyUiAsset()
            .assetId(ASSET_ID)
            .dataSourceAvailability(DataSourceAvailability.ON_REQUEST)
            .onRequestContactEmail("contact@example.com")
            .onRequestContactEmailSubject("Test")
            .build();

        // act
        var jsonLd = assetJsonLdBuilder.createAssetJsonLd(createRequest, ORG_NAME);
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    private UiAsset.UiAssetBuilder dummyUiAsset() {
        return UiAsset.builder()
            .connectorEndpoint(ENDPOINT)
            .isOwnConnector(false)
            .creatorOrganizationName(ORG_NAME)
            .participantId(PARTICPANT_ID)
            .assetId(ASSET_ID)
            .title(ASSET_ID)
            .dataSampleUrls(List.of())
            .keywords(List.of())
            .nutsLocations(List.of())
            .referenceFileUrls(List.of())
            .customJsonLdAsString("{}")
            .privateCustomJsonLdAsString("{}");
    }
}
