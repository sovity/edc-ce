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

import de.sovity.edc.ce.api.common.model.DataSourceType;
import de.sovity.edc.ce.api.common.model.SecretValue;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiDataSource;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpDataMethod;
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ce.libs.mappers.Factory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.ce.libs.mappers.JsonAssertsUtils.assertEqualJson;
import static org.mockito.Mockito.mock;

class AssetJsonLdBuilderTest {
    AssetJsonLdBuilder assetJsonLdBuilder;

    private static final String ASSET_ID = "asset-id";
    private static final String ORG_NAME = "org-name";

    @BeforeEach
    void setup() {
        var ownConnectorEndpointService = mock(OwnConnectorEndpointService.class);
        assetJsonLdBuilder = Factory.newAssetJsonLdBuilder(ownConnectorEndpointService);
    }

    @Test
    void test_create_minimal() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .build();

        var expectedProperties = Json.createObjectBuilder();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_referenceFiles_empty() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .referenceFileUrls(List.of())
            .build();

        var expectedProperties = Json.createObjectBuilder();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_dataSampleUrls_empty() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataSampleUrls(List.of())
            .build();

        var expectedProperties = Json.createObjectBuilder();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    // The following functions test paths of buildDistribution
    @Test
    void test_create_distribution_withMediaType() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .mediaType("B")
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.Dcat.MEDIATYPE, "B"));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_distribution_withConditionsForUse() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .conditionsForUse("B")
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.Dcterms.RIGHTS, Json.createObjectBuilder()
                    .add(Prop.Rdfs.LABEL, "B")));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_distribution_withDataModel() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel("B")
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                    .add(Prop.ID, "B")));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_distribution_withReferenceFileDescription() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .referenceFilesDescription("B")
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                    .add(Prop.MobilityDcatAp.SCHEMA, Json.createObjectBuilder()
                        .add(Prop.Rdfs.LITERAL, "B"))));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_distribution_nullDataModel() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(null)
            .build();

        var expected = dummyAssetJsonLd(Json.createObjectBuilder());

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, expected);
    }

    @Test
    void test_create_distribution_blankDataModel() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(" ")
            .build();

        var expected = dummyAssetJsonLd(Json.createObjectBuilder());

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, expected);
    }

    @Test
    void test_create_distribution_dataModelBlank_withReferenceFilesDesc() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(" ")
            .referenceFilesDescription("test")
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                    .add(Prop.MobilityDcatAp.SCHEMA, Json.createObjectBuilder()
                        .add(Prop.Rdfs.LITERAL, "test"))));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_distribution_dataModelBlank_withReferenceFileUrls() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(" ")
            .referenceFileUrls(List.of("http://test"))
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.SovityDcatExt.DISTRIBUTION, Json.createObjectBuilder()
                .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                    .add(Prop.MobilityDcatAp.SCHEMA, Json.createObjectBuilder()
                        .add(Prop.Dcat.DOWNLOAD_URL, Json.createArrayBuilder().add("http://test")))));

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(expectedProperties));
    }

    @Test
    void test_create_httpData_full() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .method(UiDataSourceHttpDataMethod.PUT)
                .baseUrl("https://example.com")
                .queryString("a=b")
                .headers(Map.of("c", "d", "Content-Type", "e"))
                .build())
            .build();

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.METHOD, "PUT")
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.QUERY_PARAMS, "a=b")
            .add("header:c", "d")
            .add(Prop.Edc.CONTENT_TYPE, "e");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
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

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.PROXY_METHOD, "true");

        var expectedProperties = dummyAssetCommonProperties()
            .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "true");

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
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

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.PROXY_PATH, "true");

        var expectedProperties = dummyAssetCommonProperties()
            .add(Prop.SovityDcatExt.HttpDatasourceHints.PATH, "true");

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
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

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.PROXY_QUERY_PARAMS, "true");

        var expectedProperties = dummyAssetCommonProperties()
            .add(Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS, "true");

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
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

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.PROXY_BODY, "true");

        var expectedProperties = dummyAssetCommonProperties()
            .add(Prop.SovityDcatExt.HttpDatasourceHints.BODY, "true");

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_authHeader_secretName() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .authHeaderName("X-Test")
                .authHeaderValue(SecretValue.builder().secretName("mySecretName").build())
                .build())
            .build();

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.AUTH_KEY, "X-Test")
            .add(Prop.Edc.SECRET_NAME, "mySecretName");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_authHeader_rawValue() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .authHeaderName("X-Test")
                .authHeaderValue(SecretValue.builder().rawValue("myKey").build())
                .build())
            .build();

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Prop.Edc.AUTH_KEY, "X-Test")
            .add(Prop.Edc.AUTH_CODE, "myKey");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
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

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "http://example.com/dummy/baseUrl/on-demand-asset-data-source/info-message?email=contact%40example.com&subject=Test")
            .add(Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST)
            .add(Prop.SovityDcatExt.CONTACT_EMAIL, "contact@example.com")
            .add(Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT, "Test");

        var expectedProperties = dummyAssetCommonProperties()
            .add(Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST)
            .add(Prop.SovityDcatExt.CONTACT_EMAIL, "contact@example.com")
            .add(Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT, "Test")
            .remove(Prop.SovityDcatExt.HttpDatasourceHints.METHOD)
            .remove(Prop.SovityDcatExt.HttpDatasourceHints.PATH)
            .remove(Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS)
            .remove(Prop.SovityDcatExt.HttpDatasourceHints.BODY);

        // act
        var actual = assetJsonLdBuilder.createAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertEqualJson(actual, dummyAssetJsonLd(dataAddress, expectedProperties));
    }

    private JsonObject dummyAssetJsonLd(
        JsonObjectBuilder dataAddress,
        JsonObjectBuilder properties
    ) {
        return Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
            .add(Prop.ID, ASSET_ID)
            .add(Prop.Edc.DATA_ADDRESS, dataAddress)
            .add(Prop.Edc.PROPERTIES, properties)
            .add(Prop.Edc.PRIVATE_PROPERTIES, Json.createObjectBuilder())
            .build();
    }

    private JsonObject dummyAssetJsonLd(
        JsonObjectBuilder properties
    ) {
        var dataAddress = dummyDataAddressJsonLd();
        properties = properties.addAll(dummyAssetCommonProperties());
        return dummyAssetJsonLd(dataAddress, properties);
    }

    private JsonObjectBuilder dummyAssetCommonProperties() {
        return Json.createObjectBuilder()
            .add(Prop.Edc.ID, ASSET_ID)
            .add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                .add(Prop.Foaf.NAME, ORG_NAME))
            .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "false")
            .add(Prop.SovityDcatExt.HttpDatasourceHints.PATH, "false")
            .add(Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS, "false")
            .add(Prop.SovityDcatExt.HttpDatasourceHints.BODY, "false");
    }

    private UiDataSource dummyDataSource() {
        return UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .build())
            .build();
    }

    private JsonObjectBuilder dummyDataAddressJsonLd() {
        return Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com");
    }
}
