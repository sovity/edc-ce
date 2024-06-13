package de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http;

import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


@RequiredArgsConstructor
public class HttpHeaderMapper {
    public Map<String, String> buildHeaderProps(@Nullable Map<String, String> headers) {
        return mapKeys(
            headers,
            key -> {
                if ("content-type".equalsIgnoreCase(key)) {
                    // Content-Type is overridden by a special Data Address property
                    // So we should set that instead of attempting to set a header
                    return Prop.Edc.CONTENT_TYPE;
                } else {
                    return "header:%s".formatted(key);
                }
            }
        );
    }

    private <K, L, T> Map<L, T> mapKeys(
        @Nullable Map<K, T> map,
        @NonNull Function<K, L> keyMapper
    ) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return map.entrySet().stream().collect(toMap(
            e -> keyMapper.apply(e.getKey()),
            Map.Entry::getValue,
            (v1, v2) -> v1 // lenient merge function
        ));
    }
}
