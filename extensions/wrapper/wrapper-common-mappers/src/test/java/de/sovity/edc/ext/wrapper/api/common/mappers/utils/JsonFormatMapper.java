package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

public class JsonFormatMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DCAT_KEYWORD = "http://www.w3.org/ns/dcat#keyword";
    private static final String DC_TERMS_TITLE = "http://purl.org/dc/terms/title";
    private static final String SOVITY_HINTS_PROXY_METHOD = "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyMethod";

    @SneakyThrows
    public String mapKeywords(String oldJsonStr) {
        return mapSingleValue(oldJsonStr, "keywords", DCAT_KEYWORD);
    }

    @SneakyThrows
    public String mapTitle(String oldJsonStr) {
        return mapSingleValue(oldJsonStr, "title", DC_TERMS_TITLE);
    }

    @SneakyThrows
    public String mapBooleanHintProxyMethod(String oldJsonStr) {
        return mapSingleValue(oldJsonStr, "httpDatasourceHintsProxyMethod", SOVITY_HINTS_PROXY_METHOD);
    }

    @SneakyThrows
    public String mapTitleList(String oldJsonStr) {
        JsonNode oldJson = OBJECT_MAPPER.readTree(oldJsonStr);
        ObjectNode newJson = OBJECT_MAPPER.createObjectNode();

        if (oldJson.has("title")) {
            ArrayNode titleArray = OBJECT_MAPPER.createArrayNode();
            for (JsonNode item : oldJson.get("title")) {
                ObjectNode titleObject = OBJECT_MAPPER.createObjectNode();
                titleObject.set("@value", item.get("value"));
                titleObject.set("@language", item.get("language"));
                titleArray.add(titleObject);
            }
            newJson.set(DC_TERMS_TITLE, titleArray);
        }

        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(newJson);
    }

    private String mapSingleValue(String jsonStr, String oldKey, String newKey) throws Exception {
        JsonNode oldJson = OBJECT_MAPPER.readTree(jsonStr);
        ObjectNode newJson = OBJECT_MAPPER.createObjectNode();
        newJson.set(newKey, oldJson.get(oldKey));
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(newJson);
    }
}
