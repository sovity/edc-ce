package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UiAssetMapperTest {
    UiAssetMapper uiAssetMapper;
    EdcPropertyUtils edcPropertyUtils;

    private static final String ASSET_ID = "asset-id";
    private static final String ORG_NAME = "org-name";

    @BeforeEach
    void setup() {
        var assetJsonLdUtils = mock(AssetJsonLdUtils.class);
        var markdownToTextConverter = mock(MarkdownToTextConverter.class);
        var textUtils = mock(TextUtils.class);
        var ownConnectorEndpointService = mock(OwnConnectorEndpointService.class);
        edcPropertyUtils = new EdcPropertyUtils();

        uiAssetMapper = new UiAssetMapper(edcPropertyUtils, assetJsonLdUtils, markdownToTextConverter, textUtils, ownConnectorEndpointService);
    }

    @Test
    void test_buildAssetJsonLd_only_id() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES)).hasSize(2);
        assertThat(actual.getString(Prop.ID)).isEqualTo(ASSET_ID);
    }

    @Test
    void test_buildAssetJsonLd_empty_nuts() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setNutsLocations(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES)).hasSize(2);
    }

    @Test
    void test_buildAssetJsonLd_empty_reference_file_urls() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setReferenceFileUrls(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES)).hasSize(2);
    }

    @Test
    void test_buildAssetJsonLd_empty_data_sample_urls() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataSampleUrls(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES)).hasSize(2);
    }

    // The following functions test paths of buildDistribution
    @Test
    void test_buildAssetJsonLd_distribution1() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setMediaType("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo(ASSET_ID);
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties).hasSize(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution).hasSize(1);
        assertThat(distribution.getString(Prop.Dcat.MEDIATYPE)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution2() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setConditionsForUse("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo(ASSET_ID);
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties).hasSize(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution).hasSize(1);
        var rights = distribution.getJsonObject(Prop.Dcterms.RIGHTS);
        assertThat(rights).hasSize(1);
        assertThat(rights.getString(Prop.Rdfs.LABEL)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution3() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo(ASSET_ID);
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties).hasSize(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution).hasSize(1);
        var mobilityDataStandard = distribution.getJsonObject(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD);
        assertThat(mobilityDataStandard).hasSize(1);
        assertThat(mobilityDataStandard.getString(Prop.ID)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution4() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setReferenceFilesDescription("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo(ASSET_ID);
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties).hasSize(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution).hasSize(1);
        var mobilityDataStandard = distribution.getJsonObject(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD);
        assertThat(mobilityDataStandard).hasSize(1);
        var referenceFiles = mobilityDataStandard.getJsonObject(Prop.MobilityDcatAp.SCHEMA);
        assertThat(referenceFiles).hasSize(1);
        assertThat(referenceFiles.getString(Prop.Rdfs.LITERAL)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_data_model_nonNull() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel("B");

        var expected = Json.createObjectBuilder()
                .add(Prop.Dcat.DISTRIBUTION, Json.createObjectBuilder()
                        .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                                .add(Prop.ID, "B")));

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThatJson(JsonUtils.toJson(actual))
                .isEqualTo(JsonUtils.toJson(buildAssetJsonLd(expected)));
    }

    @Test
    void test_buildAssetJsonLd_data_model_null() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel(null);

        var expected = Json.createObjectBuilder();

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThatJson(JsonUtils.toJson(actual))
                .isEqualTo(JsonUtils.toJson(buildAssetJsonLd(expected)));
    }

    @Test
    void test_buildAssetJsonLd_data_model_blank() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel(" ");

        var expected = Json.createObjectBuilder();

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThatJson(JsonUtils.toJson(actual))
                .isEqualTo(JsonUtils.toJson(buildAssetJsonLd(expected)));
    }

    @Test
    void test_buildAssetJsonLd_data_model_blank_but_also_reference_files_desc() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel(" ");
        uiAssetCreateRequest.setReferenceFilesDescription("test");

        var expected = Json.createObjectBuilder()
                .add(Prop.Dcat.DISTRIBUTION, Json.createObjectBuilder()
                        .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                                .add(Prop.MobilityDcatAp.SCHEMA, Json.createObjectBuilder()
                                        .add(Prop.Rdfs.LITERAL, "test"))));

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThatJson(JsonUtils.toJson(actual))
                .isEqualTo(JsonUtils.toJson(buildAssetJsonLd(expected)));
    }

    @Test
    void test_buildAssetJsonLd_data_model_blank_but_also_reference_file_urls() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId(ASSET_ID);
        uiAssetCreateRequest.setDataModel(" ");
        uiAssetCreateRequest.setReferenceFileUrls(List.of("http://test"));

        var expected = Json.createObjectBuilder()
                .add(Prop.Dcat.DISTRIBUTION, Json.createObjectBuilder()
                        .add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, Json.createObjectBuilder()
                                .add(Prop.MobilityDcatAp.SCHEMA, Json.createObjectBuilder()
                                        .add(Prop.Dcat.DOWNLOAD_URL, Json.createArrayBuilder().add("http://test")))));

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, ORG_NAME);

        // assert
        assertThatJson(JsonUtils.toJson(actual))
                .isEqualTo(JsonUtils.toJson(buildAssetJsonLd(expected)));
    }

    /**
     * Creates Asset JSON LD from additional properties
     * <p>
     * Let the above tests be more readable
     *
     * @param properties additional Asset JSON-LD Properties
     * @return Asset JSON LD
     */
    private JsonObject buildAssetJsonLd(JsonObjectBuilder properties) {
        return Json.createObjectBuilder()
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.ID, ASSET_ID)
                .add(Prop.Edc.DATA_ADDRESS, Json.createObjectBuilder()
                        .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
                        .add(Prop.Edc.PROPERTIES, Json.createObjectBuilder()))
                .add(Prop.Edc.PROPERTIES, properties
                        .add(Prop.Edc.ID, ASSET_ID)
                        .add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                                .add(Prop.Foaf.NAME, ORG_NAME))
                )
                .add(Prop.Edc.PRIVATE_PROPERTIES, Json.createObjectBuilder())
                .build();
    }
}
