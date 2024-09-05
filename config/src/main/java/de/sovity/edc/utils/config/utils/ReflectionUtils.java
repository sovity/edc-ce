package de.sovity.edc.utils.config.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * We want to extend {@link org.eclipse.edc.boot.system.runtime.BaseRuntime}, but unfortunately some vital fields are private.
 * <p>
 * Instead of maintaining a little bit more code, we do this. Where there's a will, there's a way.
 */
@UtilityClass
public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T> T getFieldValue(Object obj, String fieldName) {
        var field = getField(obj.getClass(), fieldName);
        return (T) field.get(obj);
    }

    @SneakyThrows
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    @SneakyThrows
    private static Field getField(Class<?> clazz, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
