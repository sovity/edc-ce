package de.sovity.edc.ext.wrapper.api.common.model.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class CustomDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    private JavaType type;

    public CustomDeserializer() {
    }

    public CustomDeserializer(JavaType type) {
        this.type = type;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        //beanProperty is null when the type to deserialize is the top-level type or a generic type, not a type of a bean property
        JavaType type = deserializationContext.getContextualType() != null
                ? deserializationContext.getContextualType()
                : beanProperty.getMember().getType();
        return new CustomDeserializer(type);
    }

    @SneakyThrows
    @Override
    public Object deserialize(JsonParser p, DeserializationContext deserializationContext) {
        if (type.isTypeOrSubTypeOf(String.class)) {
            return parseString(p);
        } else if (type.isTypeOrSubTypeOf(Boolean.class)) {
            return parseBoolean(p);
        } else if (type.isTypeOrSubTypeOf(List.class)) {
            return parseList(p);
        }
        return p.readValueAs(Object.class);
    }

    @SneakyThrows
    private String parseString(JsonParser p) {
        if (p.isExpectedStartArrayToken()) {
            return getFirstValueFromList(p.readValueAs(List.class));
        } else if (p.isExpectedStartObjectToken()) {
            return extractValueFromMap(p.readValueAs(LinkedHashMap.class));
        }
        return p.getText();
    }

    @SneakyThrows
    private Boolean parseBoolean(JsonParser p) {
        String valueAsString = p.isExpectedStartArrayToken() ? getFirstValueFromList(p.readValueAs(List.class)) : p.getText();
        if ("true".equalsIgnoreCase(valueAsString)) {
            return true;
        } else if ("false".equalsIgnoreCase(valueAsString)) {
            return false;
        }
        return null;
    }

    @SneakyThrows
    private List<String> parseList(JsonParser p) {
        if (p.isExpectedStartArrayToken()) {
            return p.readValueAs(List.class);
        }
        return Collections.singletonList(p.getText());
    }

    private String getFirstValueFromList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }
        Object firstItem = list.get(0);
        if (firstItem instanceof List) {
            return getFirstValueFromList((List<?>) firstItem);
        } else if (firstItem instanceof LinkedHashMap) {
            return extractValueFromMap((LinkedHashMap<?, ?>) firstItem);
        }
        return firstItem.toString();
    }

    @SneakyThrows
    private String extractValueFromMap(LinkedHashMap<?, ?> map) {
        Object valueObject = map.get("@value");

        if (valueObject instanceof List) {
            return getFirstValueFromList((List<?>) valueObject);
        } else if (valueObject instanceof LinkedHashMap) {
            return extractValueFromMap((LinkedHashMap<?, ?>) valueObject);
        }

        return valueObject.toString();
    }
}
