package de.sovity.edc.ext.wrapper.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Where there's a will, there's a way.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldAccessUtils {

    /**
     * Access an object's private field's values recursively.
     *
     * @param o             object
     * @param fieldNamePath field names
     * @param <T>           return type
     * @return field value
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T accessField(Object o, List<String> fieldNamePath) {
        Object result = o;
        for (String fieldName : fieldNamePath) {
            result = accessField(result, fieldName);
        }
        return (T) result;
    }

    /**
     * Access an object's private field's value.
     *
     * @param o         object
     * @param fieldName field name
     * @param <T>       return type
     * @return field value
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T accessField(Object o, String fieldName) {
        var field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(o);
    }
}
