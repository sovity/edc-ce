package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetMapper;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AssetMapperTest {
    AssetMapper assetMapper;

    @BeforeEach
    void setup() {
        var jsonLd = new TitaniumJsonLd(mock(Monitor.class));
        var typeTransformerRegistry = mock(TypeTransformerRegistry.class);
        var uiAssetBuilder = new UiAssetMapper(new EdcPropertyUtils(), jsonLd);
        assetMapper = new AssetMapper(typeTransformerRegistry, uiAssetBuilder, jsonLd);
    }

    @Test
    @SneakyThrows
    void test_buildAssetDto() {
        // Arrange
        String assetJsonLd = new String(Files.readAllBytes(Paths.get(getClass().getResource("/example-asset.jsonld").toURI())));

        // Act
        var uiAsset = assetMapper.buildUiAsset(JsonUtils.parseJsonObj(assetJsonLd));

        // Assert
        assertThat(uiAsset.getAssetId()).isEqualTo("urn:artifact:my-asset");
        assertThat(uiAsset.getName()).isEqualTo("My Asset");
        assertThat(uiAsset.getLanguage()).isEqualTo("https://w3id.org/idsa/code/EN");
        assertThat(uiAsset.getDescription()).isEqualTo("Lorem Ipsum ...");
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
        assertThat(uiAsset.getAssetJsonLd()).contains("\"%s\"".formatted(Prop.Edc.ID));
        assertThat(uiAsset.getAdditionalProperties()).containsExactlyEntriesOf(Map.of(
                "http://unknown/some-custom-string", "some-string-value"));
        assertThat(uiAsset.getAdditionalJsonProperties()).containsExactlyEntriesOf(Map.of(
                "http://unknown/some-custom-obj", "{\"http://unknown/a\":\"b\"}"));
        assertThat(uiAsset.getPrivateProperties()).containsExactlyEntriesOf(Map.of(
                "http://unknown/some-custom-private-string", "some-private-value"));
        assertThat(uiAsset.getPrivateJsonProperties()).containsExactlyEntriesOf(Map.of(
                "http://unknown/some-custom-private-obj", "{\"http://unknown/a-private\":\"b-private\"}"));
    }

    @Test
    void test_empty() {

        // Arrange
        var assetJsonLd = createObjectBuilder()
                .add(Prop.ID, "my-asset-1")
                .build();
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getAssetId()).isEqualTo("my-asset-1");
        assertThat(uiAsset.getName()).isEqualTo("my-asset-1");
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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

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
                                .add(Prop.VALUE, "AssetName")
                                .add(Prop.LANGUAGE, "en")))
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getName()).isEqualTo("AssetName");
    }

    @Test
    void test_StringsAsMap() {

        // Arrange
        var properties = createObjectBuilder()
                .add(Prop.Dcterms.TITLE, createArrayBuilder()
                        .add(createObjectBuilder()
                                .add(Prop.TYPE, "SomeType")
                                .add(Prop.VALUE, "AssetName")
                        )
                )
                .build();
        var assetJsonLd = createObjectBuilder()
                .add(Prop.ID, "my-asset-1")
                .add(Prop.Edc.PROPERTIES, properties)
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getName()).isEqualTo("AssetName");
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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

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
        var uiAsset = assetMapper.buildUiAsset(assetJsonLd);

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isNull();
    }
}
