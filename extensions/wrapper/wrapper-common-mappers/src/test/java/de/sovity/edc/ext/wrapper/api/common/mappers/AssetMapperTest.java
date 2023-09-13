package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonFormatMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetMapperTest {

    @InjectMocks
    AssetMapper assetMapper;
    @InjectMocks
    JsonFormatMapper jsonFormatMapper;

    @Test
    @SneakyThrows
    void test_buildAssetDto() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/sample.json").toURI())));

        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

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

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(jsonFormatMapper.mapKeywords("{\"keywords\":\"SingleElement\"}"));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("SingleElement"));
    }

    @Test
    @SneakyThrows
    void test_StringsAsList() {

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(jsonFormatMapper.mapTitleList(
                "{\"title\":" +
                        "[{\"value\":\"AssetName\",\"language\":\"en\"}," +
                        "{\"value\":\"AssetNameinFrench\",\"language\":\"fr\"}]}"));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetName");
    }

    @Test
    @SneakyThrows
    void test_StringsAsMap() {

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(jsonFormatMapper.mapTitle(
                "{\"title\":" +
                        "{\"@type\":\"SomeType\"," +
                        "\"@value\":\"AssetName\"}}"));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getTitle()).isEqualTo("AssetName");
    }

    @Test
    @SneakyThrows
    void test_badBooleanValue() {

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(jsonFormatMapper.mapBooleanHintProxyMethod(
                "{\"httpDatasourceHintsProxyMethod\":\"wrongBooleanValue\"}"));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }

    @Test
    @SneakyThrows
    void test_noBooleanValue() {

        // Act
        var uiAsset = assetMapper.buildUiAssetFromAssetJsonLd(jsonFormatMapper.mapBooleanHintProxyMethod("{}"));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }
}
