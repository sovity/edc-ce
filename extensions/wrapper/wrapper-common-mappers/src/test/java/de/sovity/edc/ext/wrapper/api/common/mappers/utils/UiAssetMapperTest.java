package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.val;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.value;
import static org.mockito.Mockito.mock;

class UiAssetMapperTest {
    @Test
    void test_buildAssetJsonLdContainsCustomJson() {
        // Arrange
        val mapper = new UiAssetMapper(
                mock(EdcPropertyUtils.class),
                mock(AssetJsonLdUtils.class),
                mock(MarkdownToTextConverter.class),
                mock(TextUtils.class),
                mock(OwnConnectorEndpointService.class)
        );
        val createRequest = new UiAssetCreateRequest();

        createRequest.setId("my-asset");
        createRequest.setTitle("My Asset");
        createRequest.setCustomJsonAsString("verbatim JSON string");
        createRequest.setCustomJsonLdAsString("""
                {
                    "https://a/b#string": "value",
                    "https://a/b#array": [1,2,3,4],
                    "https://a/b#null": null,
                    "https://a/b#boolean": true,
                    "https://a/b#number": 3.14,
                    "https://a/b#object": {"key": "value"}
                }
                """);

        // Act
        val jsonLd = mapper.buildAssetJsonLd(createRequest, "my-organisation");
        val serialized = JsonUtils.toJson(jsonLd);

        // Assert
        assertThatJson(serialized).isObject().containsKey(Prop.Edc.PROPERTIES);

        val properties = jsonLd.getJsonObject(Prop.Edc.PROPERTIES);
        val serializedProperties = JsonUtils.toJson(properties);
        assertThatJson(serializedProperties).isObject()
                .containsEntry(
                        "https://semantic.sovity.io/dcat-ext#customJson",
                        "verbatim JSON string")
                .containsEntry("https://a/b#string", "value")
                .containsEntry("https://a/b#boolean", true)
                .containsEntry("https://a/b#number", 3.14)
                .containsEntry("https://a/b#object", json("{\"key\": \"value\"}"))
                .containsEntry("https://a/b#array", json("[1,2,3,4]"))
                // nulls are removed
                .doesNotContainValue("https://a/b#null")
        ;

        // TODO: can override existing properties with json LD
        // TODO are there some properties that we should not override?
    }
}
