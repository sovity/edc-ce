package de.sovity.edc.ext.brokerserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtils {
    public static <K, T> Map<K, T> associateBy(Collection<T> collection, Function<T, K> keyExtractor) {
        return collection.stream().collect(Collectors.toMap(keyExtractor, Function.identity(), (a, b) -> {
            throw new IllegalStateException("Duplicate key %s.".formatted(keyExtractor.apply(a)));
        }));
    }
}
