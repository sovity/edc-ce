package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class UiAssetMapperTest {
    UiAssetMapper uiAssetMapper;
    EdcPropertyUtils edcPropertyUtils;

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
        uiAssetCreateRequest.setId("A");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES).size()).isEqualTo(2);
        assertThat(actual.getString(Prop.ID)).isEqualTo("A");
    }

    @Test
    void test_buildAssetJsonLd_empty_nuts() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setNutsLocations(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES).size()).isEqualTo(2);
    }

    @Test
    void test_buildAssetJsonLd_empty_reference_file_urls() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setReferenceFileUrls(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES).size()).isEqualTo(2);
    }

    @Test
    void test_buildAssetJsonLd_empty_data_sample_urls() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setDataSampleUrls(List.of());

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getJsonObject(Prop.Edc.PROPERTIES).size()).isEqualTo(2);
    }

    // The following functions test paths of buildDistribution
    @Test
    void test_buildAssetJsonLd_distribution1() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setMediaType("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo("A");
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties.size()).isEqualTo(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution.size()).isEqualTo(1);
        assertThat(distribution.getString(Prop.Dcat.MEDIATYPE)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution2() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setConditionsForUse("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo("A");
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties.size()).isEqualTo(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution.size()).isEqualTo(1);
        var rights = distribution.getJsonObject(Prop.Dcterms.RIGHTS);
        assertThat(rights.size()).isEqualTo(1);
        assertThat(rights.getString(Prop.Rdfs.LABEL)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution3() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setDataModel("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo("A");
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties.size()).isEqualTo(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution.size()).isEqualTo(1);
        var dataModel = distribution.getJsonObject(Prop.MobilityDcatAp.DATA_MODEL);
        assertThat(dataModel.size()).isEqualTo(1);
        assertThat(dataModel.getString(Prop.ID)).isEqualTo("B");
    }

    @Test
    void test_buildAssetJsonLd_distribution4() {
        // arrange
        var uiAssetCreateRequest = new UiAssetCreateRequest();
        uiAssetCreateRequest.setId("A");
        uiAssetCreateRequest.setReferenceFilesDescription("B");

        // act
        var actual = uiAssetMapper.buildAssetJsonLd(uiAssetCreateRequest, "org");

        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getString(Prop.ID)).isEqualTo("A");
        var properties = actual.getJsonObject(Prop.Edc.PROPERTIES);
        assertThat(properties.size()).isEqualTo(3);
        var distribution = properties.getJsonObject(Prop.Dcat.DISTRIBUTION);
        assertThat(distribution.size()).isEqualTo(1);
        var dataModel = distribution.getJsonObject(Prop.MobilityDcatAp.DATA_MODEL);
        assertThat(dataModel.size()).isEqualTo(1);
        var referenceFiles = dataModel.getJsonObject(Prop.MobilityDcatAp.SCHEMA);
        assertThat(referenceFiles.size()).isEqualTo(1);
        assertThat(referenceFiles.getString(Prop.Rdfs.LITERAL)).isEqualTo("B");
    }
}
