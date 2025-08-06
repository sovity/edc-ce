/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ce.api.common.model.DataSourceAvailability;
import de.sovity.edc.ce.api.common.model.DataSourceType;
import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiDataSource;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ce.libs.mappers.Factory;
import de.sovity.edc.ce.libs.mappers.TestUtils;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.sovity.edc.ce.libs.mappers.JsonAssertsUtils.assertEqualJson;
import static de.sovity.edc.ce.libs.mappers.JsonAssertsUtils.assertIsEqualExcludingPaths;
import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AssetJsonLdParserTest {
    AssetJsonLdBuilder assetJsonLdBuilder;
    AssetJsonLdParser assetJsonLdParser;
    ObjectMapper objectMapper;

    private static final String ASSET_ID = "asset-id";
    private static final String ORG_NAME = "org-name";
    private static final String ENDPOINT = "endpoint";
    private static final String PARTICIPANT_ID = "participant-id";

    @BeforeEach
    void setup() {
        var ownConnectorEndpointService = mock(OwnConnectorEndpointService.class);
        assetJsonLdBuilder = Factory.newAssetJsonLdBuilder(ownConnectorEndpointService);
        assetJsonLdParser = Factory.newAssetJsonLdParser(ownConnectorEndpointService);
        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @SneakyThrows
    void test_buildUiAsset_full() {
        // arrange
        var assetJsonLdString = TestUtils.loadResourceAsString("/example-asset-json-ld.json");
        var assetJsonString = TestUtils.loadResourceAsString("/example-ui-asset.json");

        var assetJsonLd = JsonUtils.parseJsonObj(assetJsonLdString);
        var assetJson = JsonUtils.parseJsonObj(assetJsonString);

        // act
        var actualUiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // assert
        var uiAssetJson = JsonUtils.parseJsonObj(objectMapper.writeValueAsString(actualUiAsset));
        var actualAssetJsonLd = JsonUtils.parseJsonObj(uiAssetJson.getString("assetJsonLd"));
        assertEqualJson(
            actualAssetJsonLd,
            assetJsonLd
        );

        assertIsEqualExcludingPaths(
            uiAssetJson,
            assetJson,
            List.of(List.of("assetJsonLd"))
        );
    }

    @Test
    void test_empty() {

        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getAssetId()).isEqualTo("my-asset-1");
        assertThat(uiAsset.getTitle()).isEqualTo("my-asset-1");
    }

    @Test
    void test_KeywordsAsSingleString() {

        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.Dcat.KEYWORDS, "SingleElement")
                .build())
            .build();
        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("SingleElement"));
    }

    @Test
    void test_StringValueWrappedInAtValue() {

        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.Dcterms.TITLE, createObjectBuilder()
                    .add(Prop.VALUE, "AssetTitle")
                    .add(Prop.LANGUAGE, "en")))
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetTitle");
    }

    @Test
    void test_jsonld_utils_deserializing_nested_value() {

        // Arrange
        var properties = createObjectBuilder()
            .add(Prop.Dcterms.TITLE, createArrayBuilder()
                .add(createObjectBuilder()
                    .add(Prop.TYPE, "SomeType")
                    .add(Prop.VALUE, "AssetTitle")
                )
            )
            .build();
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, properties)
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetTitle");
    }

    @Test
    void test_createUiAsset_booleanParsing_trueValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "true")
                .build())
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isTrue();
    }

    @Test
    void test_createUiAsset_booleanParsing_falseValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "false")
                .build())
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isFalse();
    }

    @Test
    void test_createUiAsset_booleanParsing_badValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "wrongBooleanValue")
                .build())
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isNull();
    }

    @Test
    void test_createUiAsset_booleanParsing_blankBooleanValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, " ")
                .build())
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isNull();
    }

    @Test
    void test_createUiAsset_booleanParsing_noBooleanValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                .build())
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, ENDPOINT, PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isNull();
    }

    @Test
    void test_isNotOwnConnector() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
            .add(Prop.ID, "my-asset-1")
            .build();

        // Act
        var uiAsset = assetJsonLdParser.buildUiAsset(assetJsonLd, "https://other-connector/api/dsp", PARTICIPANT_ID);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getIsOwnConnector()).isFalse();
    }

    @Test
    void test_buildUiAsset_httpData_methodParameterization() {
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
        var createAssetJson = assetJsonLdBuilder.buildCreateAssetJsonLds(createRequest, ORG_NAME);
        var jsonLd = assetJsonLdBuilder.buildAssetJsonLd(
            ASSET_ID, createAssetJson.getProperties(), createAssetJson.getPrivateProperties(), createAssetJson.getDataSource()
        );
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICIPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_buildUiAsset_httpData_pathParameterization() {
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
        var createAssetJson = assetJsonLdBuilder.buildCreateAssetJsonLds(createRequest, ORG_NAME);
        var jsonLd = assetJsonLdBuilder.buildAssetJsonLd(
            ASSET_ID, createAssetJson.getProperties(), createAssetJson.getPrivateProperties(), createAssetJson.getDataSource()
        );
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICIPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_buildUiAsset_httpData_queryParameterization() {
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
        var createAssetJsonLds = assetJsonLdBuilder.buildCreateAssetJsonLds(createRequest, ORG_NAME);
        var jsonLd = assetJsonLdBuilder.buildAssetJsonLd(
            ASSET_ID, createAssetJsonLds.getProperties(), createAssetJsonLds.getPrivateProperties(), createAssetJsonLds.getDataSource()
        );
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICIPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_buildUiAsset_httpData_bodyParameterization() {
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
        var createAssetJsonLds = assetJsonLdBuilder.buildCreateAssetJsonLds(createRequest, ORG_NAME);
        var jsonLd = assetJsonLdBuilder.buildAssetJsonLd(
            ASSET_ID, createAssetJsonLds.getProperties(), createAssetJsonLds.getPrivateProperties(), createAssetJsonLds.getDataSource()
        );
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICIPANT_ID);

        // assert
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("assetJsonLd")
            .isEqualTo(expected);
    }

    @Test
    void test_buildUiAsset_onRequest() {
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
            .httpDatasourceHintsProxyMethod(null)
            .httpDatasourceHintsProxyPath(null)
            .httpDatasourceHintsProxyQueryParams(null)
            .httpDatasourceHintsProxyBody(null)
            .build();

        // act
        var createAssetJsonLds = assetJsonLdBuilder.buildCreateAssetJsonLds(createRequest, ORG_NAME);
        var jsonLd = assetJsonLdBuilder.buildAssetJsonLd(
            ASSET_ID, createAssetJsonLds.getProperties(), createAssetJsonLds.getPrivateProperties(), createAssetJsonLds.getDataSource()
        );
        var actual = assetJsonLdParser.buildUiAsset(jsonLd, ENDPOINT, PARTICIPANT_ID);

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
            .participantId(PARTICIPANT_ID)
            .assetId(ASSET_ID)
            .title(ASSET_ID)
            .dataSampleUrls(List.of())
            .keywords(List.of())
            .referenceFileUrls(List.of())
            .customJsonLdAsString("{}")
            .privateCustomJsonLdAsString("{}")
            .httpDatasourceHintsProxyMethod(false)
            .httpDatasourceHintsProxyPath(false)
            .httpDatasourceHintsProxyQueryParams(false)
            .httpDatasourceHintsProxyBody(false);
    }
}
