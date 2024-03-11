package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AssetJsonLdUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MarkdownToTextConverter;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.TextUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetMapper;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AssetMapperTest {
    AssetMapper assetMapper;

    String endpoint = "https://my-connector/api/dsp";
    String participantId = "my-connector";

    @BeforeEach
    void setup() {
        var jsonLd = new TitaniumJsonLd(mock(Monitor.class));
        var typeTransformerRegistry = mock(TypeTransformerRegistry.class);
        var uiAssetBuilder = new UiAssetMapper(new EdcPropertyUtils(), new AssetJsonLdUtils(), new MarkdownToTextConverter(), new TextUtils(), x -> endpoint.equals(x));
        assetMapper = new AssetMapper(typeTransformerRegistry, uiAssetBuilder, jsonLd);
    }

    @Test
    @SneakyThrows
    void test_buildAssetDto() {
        // Arrange
        String assetJsonLd = TestUtils.loadResourceAsString("/example-asset.jsonld");

        // Act
        var uiAsset = assetMapper.buildUiAsset(JsonUtils.parseJsonObj(assetJsonLd), endpoint, participantId);

        // Assert
        assertThat(uiAsset.getAssetId()).isEqualTo("urn:artifact:my-asset");
        assertThat(uiAsset.getConnectorEndpoint()).isEqualTo(endpoint);
        assertThat(uiAsset.getParticipantId()).isEqualTo(participantId);
        assertThat(uiAsset.getTitle()).isEqualTo("My Asset");
        assertThat(uiAsset.getLanguage()).isEqualTo("https://w3id.org/idsa/code/EN");
        assertThat(uiAsset.getDescription()).isEqualTo(
                "# Lorem Ipsum...\n## h2 title\n[Link text Here](example.com) 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        assertThat(uiAsset.getDescriptionShortText()).isEqualTo(
                "Lorem Ipsum... h2 title Link text Here 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        assertThat(uiAsset.getIsOwnConnector()).isEqualTo(true);
        assertThat(uiAsset.getCreatorOrganizationName()).isEqualTo("My Organization Name");
        assertThat(uiAsset.getPublisherHomepage()).isEqualTo("https://data-source.my-org/about");
        assertThat(uiAsset.getLicenseUrl()).isEqualTo("https://data-source.my-org/license");
        assertThat(uiAsset.getVersion()).isEqualTo("1.1");
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("some", "keywords"));
        assertThat(uiAsset.getMediaType()).isEqualTo("application/json");
        assertThat(uiAsset.getLandingPageUrl()).isEqualTo("https://data-source.my-org/docs");
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isTrue();
        assertThat(uiAsset.getHttpDatasourceHintsProxyPath()).isTrue();
        assertThat(uiAsset.getHttpDatasourceHintsProxyQueryParams()).isTrue();
        assertThat(uiAsset.getHttpDatasourceHintsProxyBody()).isTrue();
        assertThat(uiAsset.getDataCategory()).isEqualTo("Infrastructure and Logistics");
        assertThat(uiAsset.getDataSubcategory()).isEqualTo("General Information About Planning Of Routes");
        assertThat(uiAsset.getDataModel()).isEqualTo("my-data-model-001");
        assertThat(uiAsset.getGeoReferenceMethod()).isEqualTo("my-geo-reference-method");
        assertThat(uiAsset.getTransportMode()).isEqualTo("my-geo-reference-method");
        assertThat(uiAsset.getSovereignLegalName()).isEqualTo("my-sovereign");
        assertThat(uiAsset.getGeoLocation()).isEqualTo("my-geolocation");
        assertThat(uiAsset.getNutsLocation()).isEqualTo(Arrays.asList("my-nuts-location1", "my-nuts-location2"));
        assertThat(uiAsset.getDataSampleUrls()).isEqualTo(Arrays.asList("my-data-sample-urls1", "my-data-sample-urls2"));
        assertThat(uiAsset.getReferenceFileUrls()).isEqualTo(Arrays.asList("my-reference-files1", "my-reference-files2"));
        assertThat(uiAsset.getReferenceFilesDescription()).isEqualTo("my-additional-description");
        assertThat(uiAsset.getConditionsForUse()).isEqualTo("my-conditions-for-use");
        assertThat(uiAsset.getDataUpdateFrequency()).isEqualTo("my-data-update-frequency");
        assertThat(uiAsset.getTemporalCoverageFrom()).isEqualTo("2007-12-03");
        assertThat(uiAsset.getTemporalCoverageToInclusive()).isEqualTo("2024-01-22");

        assertThat(uiAsset.getAssetJsonLd()).contains("\"%s\"".formatted(Prop.Edc.ID));

        JsonAssertions.assertThatJson(uiAsset.getCustomJsonAsString())
                .isEqualTo("""
                        {
                          "array": [3, 1, 4, 1, 5],
                          "boolean": false,
                          "null": null,
                          "number": 116,
                          "object": {
                            "key": "value"
                          },
                          "string": "value"
                        }
                        """);
        JsonAssertions.assertThatJson(uiAsset.getCustomJsonLdAsString())
                .isObject()
                .containsEntry("http://unknown/some-custom-string", "some-string-value")
                .containsEntry("http://unknown/some-custom-obj", json("""
                        { "http://unknown/a": "b" }
                        """));

        JsonAssertions.assertThatJson(uiAsset.getPrivateCustomJsonAsString())
                .isEqualTo("""
                        {
                          "priv_array": [3, 1, 4, 1, 5],
                          "priv_boolean": false,
                          "priv_null": null,
                          "priv_number": 116,
                          "priv_object": {
                            "key": "value"
                          },
                          "priv_string": "value"
                        }
                        """);
        JsonAssertions.assertThatJson(uiAsset.getPrivateCustomJsonLdAsString())
                .isObject()
                .containsEntry("http://unknown/some-custom-private-string", "some-private-value")
                .containsEntry("http://unknown/some-custom-private-obj", json("""
                        {
                            "http://unknown/a-private": "b-private"
                        }
                        """));
    }

    @Test
    void test_empty() {

        // Arrange
        var assetJsonLd = createObjectBuilder()
                .add(Prop.ID, "my-asset-1")
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetTitle");
    }

    @Test
    void test_StringsAsMap() {

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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetTitle");
    }

    @Test
    void test_badBooleanValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
                .add(Prop.ID, "my-asset-1")
                .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                        .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "wrongBooleanValue")
                        .build())
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isNull();
    }

    @Test
    void test_noBooleanValue() {
        // Arrange
        var assetJsonLd = createObjectBuilder()
                .add(Prop.ID, "my-asset-1")
                .add(Prop.Edc.PROPERTIES, createObjectBuilder()
                        .add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "")
                        .build())
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, endpoint, participantId);

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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd, "https://other-connector/api/dsp", participantId);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getIsOwnConnector()).isFalse();
    }
}
