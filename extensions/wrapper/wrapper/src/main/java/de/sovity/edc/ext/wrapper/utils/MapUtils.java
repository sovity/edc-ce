package de.sovity.edc.ext.wrapper.utils;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MapUtils {

    public static <K, T, R> Map<K, R> mapValues(@NonNull Map<K, T> map, @NonNull Function<T, R> valueMapper) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> valueMapper.apply(e.getValue())));
    }
}
