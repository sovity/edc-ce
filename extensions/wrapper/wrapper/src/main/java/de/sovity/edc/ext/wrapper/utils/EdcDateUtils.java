package de.sovity.edc.ext.wrapper.utils;

import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EdcDateUtils {

    /**
     * Build {@link OffsetDateTime} from UTC milliseconds since epoch.
     * <p>
     * The EDC framework only uses longs to represent dates.
     * <p>
     * We want to use real date types in our code.
     *
     * @param utcMillis milliseconds since epoch in UTC
     * @return {@link OffsetDateTime} or null
     */
    public static OffsetDateTime utcMillisToOffsetDateTime(Long utcMillis) {
        if (utcMillis == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(utcMillis), java.time.ZoneOffset.UTC);
    }
}
