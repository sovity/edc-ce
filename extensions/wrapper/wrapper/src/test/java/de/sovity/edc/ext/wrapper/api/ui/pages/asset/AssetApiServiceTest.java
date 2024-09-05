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
package de.sovity.edc.ext.wrapper.api.ui.pages.asset;


import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.UiAsset;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetEditRequest;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.FailedMappingException;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.RuntimePerClassWithDbExtension;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
public class AssetApiServiceTest {

    public static final String DATA_SINK = "http://my-data-sink/api/stuff";

    @RegisterExtension
    static RuntimePerClassWithDbExtension providerExtension = CeIntegrationTestUtils.defaultIntegrationTest();
    EdcClient client;
    EdcPropertyUtils edcPropertyUtils;

    @BeforeEach
    void setup(Config config) {
        client = CeIntegrationTestUtils.getEdcClient(config);
        edcPropertyUtils = new EdcPropertyUtils();
    }

    @Test
    void assetPage(AssetService assetService) {
        // arrange
        var properties = Map.of(
            Asset.PROPERTY_ID, "asset-11",
            Prop.Dcat.LANDING_PAGE, "https://data-source.my-org/docs"
        );
        createAsset(assetService, "2023-06-01", properties);

        // act
        var result = client.uiApi().getAssetPage();

        // assert
        var assets = result.getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo(properties.get(Asset.PROPERTY_ID));
        assertThat(asset.getLandingPageUrl()).isEqualTo(properties.get(Prop.Dcat.LANDING_PAGE));
    }

    @Test
    void assetPageSorting(AssetService assetService) {
        // arrange
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-21"));
        createAsset(assetService, "2023-06-03", Map.of(Asset.PROPERTY_ID, "asset-23"));
        createAsset(assetService, "2023-06-02", Map.of(Asset.PROPERTY_ID, "asset-22"));

        // act
        var result = client.uiApi().getAssetPage();

        // assert
        assertThat(result.getAssets())
            .extracting(UiAsset::getAssetId)
            .containsExactly("asset-23", "asset-22", "asset-21");
    }

    @Test
    void testAssetCreation(AssetService assetService) {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(DATA_SINK)
                .enableMethodParameterization(true)
                .enablePathParameterization(true)
                .enableQueryParameterization(true)
                .enableBodyParameterization(true)
                .build())
            .customProperties(Map.of("oauth2:tokenUrl", "https://token-url"))
            .build();
        var uiAssetRequest = UiAssetCreateRequest.builder()
            .id("asset-31")
            .title("AssetTitle")
            .description("AssetDescription")
            .licenseUrl("https://license-url")
            .version("1.0.0")
            .language("en")
            .mediaType("application/json")
            .dataCategory("dataCategory")
            .dataSubcategory("dataSubcategory")
            .dataModel("dataModel")
            .geoReferenceMethod("geoReferenceMethod")
            .transportMode("transportMode")
            .sovereignLegalName("my sovereign")
            .geoLocation("40.0, 40.0")
            .nutsLocations(Arrays.asList("DE", "DE929"))
            .dataSampleUrls(Arrays.asList("https://sample-a", "https://sample-b"))
            .referenceFileUrls(Arrays.asList("https://reference-a", "https://reference-b"))
            .referenceFilesDescription("RF Description")
            .conditionsForUse("Conditions for use")
            .dataUpdateFrequency("every month")
            .temporalCoverageFrom(LocalDate.of(2020, 1, 1))
            .temporalCoverageToInclusive(LocalDate.of(2020, 1, 8))
            .keywords(List.of("keyword1", "keyword2"))
            .publisherHomepage("publisherHomepage")
            .dataSource(dataSource)
            .customJsonAsString("""
                { "test" : "value" }
                """)
            .customJsonLdAsString("""
                {
                    "https://string": "value",
                    "https://number": 3.14,
                    "https://array": [1,2,3],
                    "https://object": { "https://key": "value" }
                }
                """)
            .privateCustomJsonAsString("""
                { "private test" : "private value" }
                """)
            .privateCustomJsonLdAsString("""
                {
                    "https://private/string": "value",
                    "https://private/number": 3.14,
                    "https://private/array": [1,2,3],
                    "https://private/object": { "https://key": "value" }
                }
                """)
            .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-31");

        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo("asset-31");
        assertThat(asset.getTitle()).isEqualTo("AssetTitle");
        assertThat(asset.getDescription()).isEqualTo("AssetDescription");
        assertThat(asset.getVersion()).isEqualTo("1.0.0");
        assertThat(asset.getLanguage()).isEqualTo("en");
        assertThat(asset.getMediaType()).isEqualTo("application/json");
        assertThat(asset.getDataCategory()).isEqualTo("dataCategory");
        assertThat(asset.getDataSubcategory()).isEqualTo("dataSubcategory");
        assertThat(asset.getDataModel()).isEqualTo("dataModel");
        assertThat(asset.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
        assertThat(asset.getTransportMode()).isEqualTo("transportMode");
        assertThat(asset.getSovereignLegalName()).isEqualTo("my sovereign");
        assertThat(asset.getGeoLocation()).isEqualTo("40.0, 40.0");
        assertThat(asset.getNutsLocations()).isEqualTo(Arrays.asList("DE", "DE929"));
        assertThat(asset.getDataSampleUrls()).isEqualTo(Arrays.asList("https://sample-a", "https://sample-b"));
        assertThat(asset.getReferenceFileUrls()).isEqualTo(Arrays.asList("https://reference-a", "https://reference-b"));
        assertThat(asset.getReferenceFilesDescription()).isEqualTo("RF Description");
        assertThat(asset.getConditionsForUse()).isEqualTo("Conditions for use");
        assertThat(asset.getDataUpdateFrequency()).isEqualTo("every month");
        assertThat(asset.getTemporalCoverageFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(asset.getTemporalCoverageToInclusive()).isEqualTo(LocalDate.of(2020, 1, 8));
        assertThat(asset.getLicenseUrl()).isEqualTo("https://license-url");
        assertThat(asset.getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(asset.getCreatorOrganizationName()).isEqualTo("Curator Name MyEDC");
        assertThat(asset.getPublisherHomepage()).isEqualTo("publisherHomepage");
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isTrue();
        assertThatJson(asset.getCustomJsonAsString()).isEqualTo("""
            { "test": "value" }
            """);
        assertThatJson(asset.getCustomJsonLdAsString()).isEqualTo("""
            {
                "https://string": "value",
                "https://number": 3.14,
                "https://array": [1.0, 2.0, 3.0],
                "https://object": { "https://key": "value" }
            }
            """);
        assertThatJson(asset.getPrivateCustomJsonAsString()).isEqualTo("""
            { "private test": "private value" }
            """);
        assertThatJson(asset.getPrivateCustomJsonLdAsString()).isEqualTo("""
            {
                "https://private/string": "value",
                "https://private/number": 3.14,
                "https://private/array": [1.0, 2.0, 3.0],
                "https://private/object": { "https://key": "value" }
            }
            """);

        var assetWithDataAddress = assetService.query(QuerySpec.max()).orElseThrow(FailedMappingException::ofFailure).toList().get(0);
        assertThat(assetWithDataAddress.getDataAddress().getProperties()).containsEntry("oauth2:tokenUrl", "https://token-url");
    }

    @Test
    void testeditAsset(AssetService assetService) {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(DATA_SINK)
                .enableMethodParameterization(true)
                .enablePathParameterization(true)
                .enableQueryParameterization(true)
                .enableBodyParameterization(true)
                .build())
            .customProperties(Map.of("oauth2:tokenUrl", "https://token-url"))
            .build();
        var createRequest = UiAssetCreateRequest.builder()
            .id("asset-41")
            .title("AssetTitle")
            .description("AssetDescription")
            .licenseUrl("https://license-url")
            .version("1.0.0")
            .language("en")
            .mediaType("application/json")
            .dataCategory("dataCategory")
            .dataSubcategory("dataSubcategory")
            .dataModel("dataModel")
            .geoReferenceMethod("geoReferenceMethod")
            .transportMode("transportMode")
            .sovereignLegalName("my sovereign")
            .geoLocation("40.0, 40.0")
            .nutsLocations(Arrays.asList("DE", "DE929"))
            .dataSampleUrls(Arrays.asList("https://sample-a", "https://sample-b"))
            .referenceFileUrls(Arrays.asList("https://reference-a", "https://reference-b"))
            .referenceFilesDescription("RF Description")
            .conditionsForUse("Conditions for use")
            .dataUpdateFrequency("every month")
            .temporalCoverageFrom(LocalDate.of(2020, 1, 1))
            .temporalCoverageToInclusive(LocalDate.of(2020, 1, 8))
            .keywords(List.of("keyword1", "keyword2"))
            .publisherHomepage("publisherHomepage")
            .dataSource(dataSource)
            .customJsonAsString("""
                { "test": "value" }
                """)
            .customJsonLdAsString("""
                {
                    "https://to-change": "value1",
                    "https://for-deletion": "value2"
                }
                """)
            .build();

        client.uiApi().createAsset(createRequest);
        var dataAddressBeforeEdit = assetService.query(QuerySpec.max())
            .orElseThrow(FailedMappingException::ofFailure).toList().get(0)
            .getDataAddress()
            .getProperties();

        var editRequest = UiAssetEditRequest.builder()
            .title("AssetTitle 2")
            .description("AssetDescription 2")
            .licenseUrl("https://license-url/2")
            .version("2.0.0")
            .language("de")
            .mediaType("application/json+utf8")
            .dataCategory("dataCategory2")
            .dataSubcategory("dataSubcategory2")
            .dataModel("dataModel2")
            .geoReferenceMethod("geoReferenceMethod2")
            .sovereignLegalName("my sovereign2")
            .geoLocation("50.0, 50.0")
            .nutsLocations(Arrays.asList("NL", "NL929"))
            .dataSampleUrls(Arrays.asList("https://sample-a2", "https://sample-b2"))
            .referenceFileUrls(Arrays.asList("https://reference-a2", "https://reference-b2"))
            .referenceFilesDescription("RF Description2")
            .conditionsForUse("Conditions for use2")
            .dataUpdateFrequency("every week")
            .temporalCoverageFrom(LocalDate.of(2021, 1, 1))
            .temporalCoverageToInclusive(LocalDate.of(2021, 1, 8))
            .transportMode("transportMode2")
            .keywords(List.of("keyword3"))
            .publisherHomepage("publisherHomepage2")
            .customJsonAsString("""
                { "edited": "new value" }
                """)
            .customJsonLdAsString("""
                {
                    "https://to-change": "new value LD",
                    "https://for-deletion": null
                }
                """)
            .build();

        // act
        var response = client.uiApi().editAsset("asset-41", editRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-41");

        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getAssetId()).isEqualTo("asset-41");
        assertThat(asset.getTitle()).isEqualTo("AssetTitle 2");
        assertThat(asset.getDescription()).isEqualTo("AssetDescription 2");
        assertThat(asset.getVersion()).isEqualTo("2.0.0");
        assertThat(asset.getLanguage()).isEqualTo("de");
        assertThat(asset.getMediaType()).isEqualTo("application/json+utf8");
        assertThat(asset.getDataCategory()).isEqualTo("dataCategory2");
        assertThat(asset.getDataSubcategory()).isEqualTo("dataSubcategory2");
        assertThat(asset.getDataModel()).isEqualTo("dataModel2");
        assertThat(asset.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod2");
        assertThat(asset.getTransportMode()).isEqualTo("transportMode2");
        assertThat(asset.getSovereignLegalName()).isEqualTo("my sovereign2");
        assertThat(asset.getGeoLocation()).isEqualTo("50.0, 50.0");
        assertThat(asset.getNutsLocations()).isEqualTo(Arrays.asList("NL", "NL929"));
        assertThat(asset.getDataSampleUrls()).isEqualTo(Arrays.asList("https://sample-a2", "https://sample-b2"));
        assertThat(asset.getReferenceFileUrls()).isEqualTo(Arrays.asList("https://reference-a2", "https://reference-b2"));
        assertThat(asset.getReferenceFilesDescription()).isEqualTo("RF Description2");
        assertThat(asset.getConditionsForUse()).isEqualTo("Conditions for use2");
        assertThat(asset.getDataUpdateFrequency()).isEqualTo("every week");
        assertThat(asset.getTemporalCoverageFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(asset.getTemporalCoverageToInclusive()).isEqualTo(LocalDate.of(2021, 1, 8));
        assertThat(asset.getLicenseUrl()).isEqualTo("https://license-url/2");
        assertThat(asset.getKeywords()).isEqualTo(List.of("keyword3"));
        assertThat(asset.getCreatorOrganizationName()).isEqualTo("Curator Name MyEDC");
        assertThat(asset.getPublisherHomepage()).isEqualTo("publisherHomepage2");
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isTrue();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isTrue();
        assertThat(asset.getCustomJsonAsString()).isEqualTo("""
            { "edited": "new value" }
            """);
        assertThatJson(asset.getCustomJsonLdAsString()).isEqualTo("""
            { "https://to-change": "new value LD" }
            """);

        var dataAddressAfterEdit = assetService.query(QuerySpec.max())
            .orElseThrow(FailedMappingException::ofFailure).toList().get(0)
            .getDataAddress()
            .getProperties();
        assertThat(dataAddressAfterEdit).isEqualTo(dataAddressBeforeEdit);
    }

    @Test
    void testAssetCreation_noProxying() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(DATA_SINK)
                .build())
            .build();
        var uiAssetRequest = UiAssetCreateRequest.builder()
            .id("asset-51")
            .dataSource(dataSource)
            .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-51");
        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isFalse();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isFalse();
    }

    @Test
    void testAssetCreation_differentDataAddressType() {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.CUSTOM)
            .customProperties(Map.of(
                Prop.Edc.TYPE, "Unknown"
            ))
            .build();
        var uiAssetRequest = UiAssetCreateRequest.builder()
            .id("asset-61")
            .dataSource(dataSource)
            .build();

        // act
        var response = client.uiApi().createAsset(uiAssetRequest);

        // assert
        assertThat(response.getId()).isEqualTo("asset-61");
        var assets = client.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);
        assertThat(asset.getHttpDatasourceHintsProxyMethod()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyPath()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isNull();
        assertThat(asset.getHttpDatasourceHintsProxyBody()).isNull();
    }

    @Test
    void testDeleteAsset(AssetService assetService) {
        // arrange
        createAsset(assetService, "2023-06-01", Map.of(Asset.PROPERTY_ID, "asset-71"));
        assertThat(assetService.query(QuerySpec.max()).getContent()).isNotEmpty();

        // act
        var response = client.uiApi().deleteAsset("asset-71");

        // assert
        assertThat(response.getId()).isEqualTo("asset-71");
        assertThat(assetService.query(QuerySpec.max()).getContent()).isEmpty();
    }

    private void createAsset(
        AssetService assetService,
        String date,
        Map<String, String> properties
    ) {
        DataAddress dataAddress = DataAddress.Builder.newInstance()
            .type("HttpData")
            .property(Prop.Edc.BASE_URL, DATA_SINK)
            .build();

        var asset = Asset.Builder.newInstance()
            .id(properties.get(Asset.PROPERTY_ID))
            .properties(edcPropertyUtils.toMapOfObject(properties))
            .dataAddress(dataAddress)
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
