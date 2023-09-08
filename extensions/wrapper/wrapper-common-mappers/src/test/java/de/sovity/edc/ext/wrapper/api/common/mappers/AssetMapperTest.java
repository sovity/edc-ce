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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetMapperTest {
    @Mock
    ObjectMapper jsonLdObjectMapper;

    @InjectMocks
    AssetMapper assetMapper;

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
        assertThat(uiAsset.getTitle()).isEqualTo("AssetName");

        // Convert the UiAsset to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String resultJson = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(uiAsset);

        // Write the result to an external file
        Files.write(Paths.get("result.json"), resultJson.getBytes());
    }
}

