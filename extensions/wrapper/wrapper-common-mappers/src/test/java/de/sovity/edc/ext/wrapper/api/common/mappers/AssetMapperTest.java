package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetMapperTest {

    @InjectMocks
    AssetMapper assetMapper;


    //for sample and then the other edge cases
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
        assertThat(uiAsset.getLicense()).isEqualTo("https://data-source.my-org/license");
        assertThat(uiAsset.getVersion()).isEqualTo("1.1");
        assertThat(uiAsset.getLanguage()).isEqualTo("https://w3id.org/idsa/code/EN");
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("some", "keywords"));
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(true);
        assertThat(uiAsset.getHttpDatasourceHintsProxyPath()).isEqualTo(true);
        assertThat(uiAsset.getDistribution()).isEqualTo("application/json");

        // Convert the UiAsset to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String resultJson = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(uiAsset);

        Files.write(Paths.get("result.json"), resultJson.getBytes());
    }
    @Test
    @SneakyThrows
    void test_KeywordsAsSingleString() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/KeywordCase.json").toURI())));
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getKeywords()).isEqualTo(List.of("SingleElement"));
    }

    @Test
    @SneakyThrows
    void test_StringsAsList() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/StringAsList.json").toURI())));
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getName()).isEqualTo("urn:artifact:my-asset");
    }

    @Test
    @SneakyThrows
    void test_StringsAsMap() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/StringAsMap.json").toURI())));
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getName()).isEqualTo("urn:artifact:my-asset");
    }

    @Test
    @SneakyThrows
    void test_badBooleanValue() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/BadBooleanValue.json").toURI())));
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }

    @Test
    @SneakyThrows
    void test_noBooleanValue() {
        // Arrange
        String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/NoBooleanValue.json").toURI())));
        // Act
        var uiAsset = assetMapper.buildUiAsset(assetMapper.buildHelperDto(jsonContent));

        // Assert
        assertThat(uiAsset).isNotNull();
        assertThat(uiAsset.getHttpDatasourceHintsProxyMethod()).isEqualTo(null);
    }
}

