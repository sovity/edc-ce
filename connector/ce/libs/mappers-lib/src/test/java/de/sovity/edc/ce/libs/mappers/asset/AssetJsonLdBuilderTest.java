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
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiAssetExtForSphinx;
import de.sovity.edc.ce.api.common.model.UiDataSource;
import de.sovity.edc.ce.api.common.model.UiDataSourceAzureStorage;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpDataMethod;
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ce.api.common.model.UiHttpAuth;
import de.sovity.edc.ce.api.common.model.UiHttpAuthApiKey;
import de.sovity.edc.ce.api.common.model.UiHttpAuthBasic;
import de.sovity.edc.ce.api.common.model.UiHttpAuthOauth2;
import de.sovity.edc.ce.api.common.model.UiHttpAuthType;
import de.sovity.edc.ce.api.common.model.UiHttpOauth2AuthType;
import de.sovity.edc.ce.api.common.model.UiHttpOauth2PrivateKeyAuthorization;
import de.sovity.edc.ce.api.common.model.UiHttpOauth2SharedSecretAuthorization;
import de.sovity.edc.ce.libs.mappers.Factory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.edc.iam.oauth2.spi.Oauth2DataAddressSchema;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.ce.libs.mappers.JsonAssertsUtils.assertEqualJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
    }

    @Test
    void test_empty_sphinx_fields() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .sphinxFields(UiAssetExtForSphinx.builder().build())
            .build();

        var expectedProperties = Json.createObjectBuilder();

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
    }

    @Test
    void test_sphinx_fields() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .sphinxFields(UiAssetExtForSphinx.builder()
                .patientCount("3000")
                .birthYearMin("1990")
                .birthYearMax("2000")
                .administrativeGender("Male")
                .bodyHeightMin("150")
                .bodyHeightMax("210")
                .diagnosisPrimary("G30 - Alzheimer disease")
                .diagnosisSecondary("I11 - Hypertensive heart disease")
                .encounterStart("2020")
                .encounterEnd("2025")
                .medicationCount("950")
                .dosageCount("930")
                .clinicalSpecialty("Neurology")
                .build())
            .build();

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.Sphinx.PATIENT_COUNT, "3000")
            .add(Prop.Sphinx.BIRTH_YEAR_MIN, "1990")
            .add(Prop.Sphinx.BIRTH_YEAR_MAX, "2000")
            .add(Prop.Sphinx.ADMINISTRATIVE_GENDER, "Male")
            .add(Prop.Sphinx.BODY_HEIGHT_MIN, "150")
            .add(Prop.Sphinx.BODY_HEIGHT_MAX, "210")
            .add(Prop.Sphinx.DIAGNOSIS_PRIMARY, "G30 - Alzheimer disease")
            .add(Prop.Sphinx.DIAGNOSIS_SECONDARY, "I11 - Hypertensive heart disease")
            .add(Prop.Sphinx.ENCOUNTER_START, "2020")
            .add(Prop.Sphinx.ENCOUNTER_END, "2025")
            .add(Prop.Sphinx.MEDICATION_COUNT, "950")
            .add(Prop.Sphinx.DOSAGE_COUNT, "930")
            .add(Prop.Sphinx.CLINICAL_SPECIALTY, "Neurology");

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
    }

    @Test
    void test_create_distribution_nullDataModel() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(null)
            .build();

        var expected = dummyBuildCreateAssetJsonLds(Json.createObjectBuilder());

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, expected);
    }

    @Test
    void test_create_distribution_blankDataModel() {
        // arrange
        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dummyDataSource())
            .id(ASSET_ID)
            .dataModel(" ")
            .build();

        var expected = dummyBuildCreateAssetJsonLds(Json.createObjectBuilder());

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, expected);
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_auth_apiKeyFromVault_secretName() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .auth(UiHttpAuth.builder()
                    .type(UiHttpAuthType.API_KEY)
                    .apiKey(UiHttpAuthApiKey.builder()
                        .headerName("X-Test")
                        .vaultKey("mySecretName")
                        .build())
                    .build())
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_auth_basic() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .auth(UiHttpAuth.builder()
                    .type(UiHttpAuthType.BASIC)
                    .basic(UiHttpAuthBasic.builder()
                        .username("foo")
                        .password("password")
                        .build())
                    .build())
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
            .add(Prop.Edc.AUTH_KEY, "Authorization")
            .add(Prop.Edc.AUTH_CODE, "Basic Zm9vOnBhc3N3b3Jk");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_auth_oauth_clientSecretKey() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .auth(UiHttpAuth.builder()
                    .type(UiHttpAuthType.OAUTH2)
                    .oauth(UiHttpAuthOauth2.builder()
                        .type(UiHttpOauth2AuthType.SHARED_SECRET)
                        .sharedSecret(UiHttpOauth2SharedSecretAuthorization.builder()
                            .clientId("clientId")
                            .clientSecretName("secretKey")
                            .build())
                        .tokenUrl("https://example.com/token")
                        .scope("scope")
                        .build())
                    .build())
                .build())
            .build();

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        // ensure key's stability
        assertThat(Oauth2DataAddressSchema.TOKEN_URL).isEqualTo("oauth2:tokenUrl");
        assertThat(Oauth2DataAddressSchema.SCOPE).isEqualTo("oauth2:scope");
        assertThat(Oauth2DataAddressSchema.CLIENT_ID).isEqualTo("oauth2:clientId");
        assertThat(Oauth2DataAddressSchema.CLIENT_SECRET_KEY).isEqualTo("oauth2:clientSecretKey");
        assertThat(Oauth2DataAddressSchema.VALIDITY).isEqualTo("oauth2:validity");
        assertThat(Oauth2DataAddressSchema.PRIVATE_KEY_NAME).isEqualTo("oauth2:privateKeyName");

        var dataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)
            .add(Prop.Edc.BASE_URL, "https://example.com")
            .add(Oauth2DataAddressSchema.TOKEN_URL, "https://example.com/token")
            .add(Oauth2DataAddressSchema.SCOPE, "scope")
            .add(Oauth2DataAddressSchema.CLIENT_ID, "clientId")
            .add(Oauth2DataAddressSchema.CLIENT_SECRET_KEY, "secretKey");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
    }

    @Test
    void test_create_httpData_auth_oauth_privateKey() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://example.com")
                .auth(UiHttpAuth.builder()
                    .type(UiHttpAuthType.OAUTH2)
                    .oauth(UiHttpAuthOauth2.builder()
                        .type(UiHttpOauth2AuthType.PRIVATE_KEY)
                        .privateKey(UiHttpOauth2PrivateKeyAuthorization.builder()
                            .privateKeyName("privateKeyName")
                            .tokenValidityInSeconds(123456789L)
                            .build())
                        .tokenUrl("https://example.com/token")
                        .scope("scope")
                        .build())
                    .build())
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
            .add(Oauth2DataAddressSchema.TOKEN_URL, "https://example.com/token")
            .add(Oauth2DataAddressSchema.SCOPE, "scope")
            .add(Oauth2DataAddressSchema.PRIVATE_KEY_NAME, "privateKeyName")
            .add(Oauth2DataAddressSchema.VALIDITY, "123456789");

        var expectedProperties = dummyAssetCommonProperties();

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
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
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(dataAddress, expectedProperties));
    }

    @Test
    void test_create_azureStorage() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.AZURE_STORAGE)
            .azureStorage(
                UiDataSourceAzureStorage.builder()
                    .accountKey("key")
                    .storageAccountName("account")
                    .containerName("container")
                    .blobName("blob")
                    .build()
            ).build();

        var uiAssetCreateRequest = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(ASSET_ID)
            .build();

        var expectedDataAddress = Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.TYPE, Prop.Edc.AZURE_BLOB_STORE_TYPE)
            .add(Prop.Edc.AZURE_ACCOUNT_NAME, "account")
            .add(Prop.Edc.AZURE_CONTAINER_NAME, "container")
            .add(Prop.Edc.AZURE_BLOB_NAME, "blob")
            .add(DataAddress.EDC_DATA_ADDRESS_KEY_NAME, "key");

        var expectedProperties = Json.createObjectBuilder()
            .add(Prop.Edc.ID, ASSET_ID)
            .add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                .add(Prop.Foaf.NAME, ORG_NAME));

        // act
        var actual = assetJsonLdBuilder.buildCreateAssetJsonLds(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertCreateAssetJsonLdsEquals(actual, dummyBuildCreateAssetJsonLds(expectedDataAddress, expectedProperties));
    }

    private AssetJsonLdBuilder.CreateAssetJsonLds dummyBuildCreateAssetJsonLds(
        JsonObjectBuilder dataAddress,
        JsonObjectBuilder properties
    ) {
        return AssetJsonLdBuilder.CreateAssetJsonLds.builder()
            .properties(properties.build())
            .dataSource(dataAddress.build())
            .privateProperties(Json.createObjectBuilder().build()).build();
    }

    private AssetJsonLdBuilder.CreateAssetJsonLds dummyBuildCreateAssetJsonLds(
        JsonObjectBuilder properties
    ) {
        var dataAddress = dummyDataAddressJsonLd();
        properties = properties.addAll(dummyAssetCommonProperties());
        return dummyBuildCreateAssetJsonLds(dataAddress, properties);
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

    private void assertCreateAssetJsonLdsEquals(AssetJsonLdBuilder.CreateAssetJsonLds actual, AssetJsonLdBuilder.CreateAssetJsonLds expected) {
        assertEqualJson(actual.getDataSource(), expected.getDataSource());
        assertEqualJson(actual.getProperties(), expected.getProperties());
        assertEqualJson(actual.getPrivateProperties(), expected.getPrivateProperties());
    }
}
