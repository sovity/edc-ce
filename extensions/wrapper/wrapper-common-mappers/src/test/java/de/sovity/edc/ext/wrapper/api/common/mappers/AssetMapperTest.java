package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetMapperTest {

    @InjectMocks
    AssetMapper assetMapper;

    @Test
    @SneakyThrows
    void test_buildAssetDto() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/sample.json").toURI())));

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetHelper(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getName()).isEqualTo("urn:artifact:my-asset");
        assertThat(uiAsset.getTitle()).isEqualTo("My Asset");
        assertThat(uiAsset.getDescription()).isEqualTo("Lorem Ipsum ...");
        assertThat(uiAsset.getPublisher()).isEqualTo("https://data-source.my-org/about");
        assertThat(uiAsset.getCreator()).isEqualTo("My Organization Name");
        assertThat(uiAsset.getLicenseUrl()).isEqualTo("https://data-source.my-org/license");
        assertThat(uiAsset.getVersion()).isEqualTo("1.1");
        assertThat(uiAsset.getLanguage()).isEqualTo("https://w3id.org/idsa/code/EN");
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("some", "keywords"));
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(true);
        assertThat(uiAsset.getHttpDatasourceHintsProxyPath()).isEqualTo(true);
        assertThat(uiAsset.getDistribution()).isEqualTo("application/json");
    }

    @Test
    @SneakyThrows
    void test_KeywordsAsSingleString() {

        // Arrange
        var requestBody = createObjectBuilder()
                .add(Prop.Dcat.KEYWORDS, createArrayBuilder(List.of("SingleElement")))
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(requestBody.toString());

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("SingleElement"));
    }

    @Test
    @SneakyThrows
    void test_StringsAsList() {

        // Arrange
        var requestBody = createObjectBuilder()
                .add(Prop.DCMI.title, createObjectBuilder()
                        .add(Prop.VALUE, "AssetName")
                        .add(Prop.LANGUAGE, "en"))
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(requestBody.toString());

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetName");
    }

    @Test
    @SneakyThrows
    void test_StringsAsMap() {

        // Arrange
        var requestBody = createObjectBuilder()
                .add(Prop.DCMI.title, createArrayBuilder()
                        .add(createObjectBuilder()
                                .add(Prop.TYPE, "SomeType")
                                .add(Prop.VALUE, "AssetName")
                        )
                )
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(requestBody.toString());

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetName");
    }

    @Test
    @SneakyThrows
    void test_badBooleanValue() {

        //Arrange
        var requestBody = createObjectBuilder()
                .add(Prop.SOVITYSEMANTIC.METHOD, "wrongBooleanValue")
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(requestBody.toString());

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }

    @Test
    @SneakyThrows
    void test_noBooleanValue() {

        //Arrange
        var requestBody = createObjectBuilder()
                .add(Prop.SOVITYSEMANTIC.METHOD, "")
                .build();

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(requestBody.toString());

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }
}
