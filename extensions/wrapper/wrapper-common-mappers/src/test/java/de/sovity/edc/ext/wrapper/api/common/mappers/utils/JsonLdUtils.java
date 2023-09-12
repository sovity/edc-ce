package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

public class JsonLdUtils {
/*
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ValueType getValueType(String jsonLd, String propertyUri) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonLd);
        JsonNode propertyNode = rootNode.get(propertyUri);

        if (propertyNode == null) {
            throw new IllegalArgumentException("Property URI not found in provided JSON-LD.");
        }

        if (propertyNode.isTextual()) {
            return ValueType.STRING;
        }

        if (propertyNode.isArray()) {
            for (JsonNode node : propertyNode) {
                if (!node.isTextual()) {
                    return ValueType.OTHER;
                }
            }
            return ValueType.ARRAY_OF_STRINGS;
        }

        return ValueType.OTHER;
    }

    public enum ValueType {
        STRING,
        ARRAY_OF_STRINGS,
        OTHER
    }

    public Map<String, ValueType> inspectPropertyTypes() {
        Map<String, ValueType> propertyTypes = new HashMap<>();

        for (Field field : UiAssetHelperDto.class.getDeclaredFields()) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                String uri = jsonProperty.value();
                ValueType valueType = determineValueType(field.getType());
                propertyTypes.put(uri, valueType);
            }
        }

        return propertyTypes;
    }

    private ValueType determineValueType(Class<?> type) {
        if (type.equals(String.class)) {
            return ValueType.STRING;
        } else if (type.equals(List.class)) {
            return ValueType.ARRAY_OF_STRINGS;
        } else {
            return ValueType.OTHER;
        }
    }

    public enum ValueType {
        STRING,
        ARRAY_OF_STRINGS,
        OTHER
    }*/

}
