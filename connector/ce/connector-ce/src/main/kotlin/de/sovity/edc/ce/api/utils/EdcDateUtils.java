/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils;

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

    /**
     * Build {@link OffsetDateTime} from UTC seconds since epoch.
     * <p>
     * The EDC framework only uses longs to represent dates.
     * <p>
     * We want to use real date types in our code.
     *
     * @param utcSeconds seconds since epoch in UTC
     * @return {@link OffsetDateTime} or null
     */
    public static OffsetDateTime utcSecondsToOffsetDateTime(Long utcSeconds) {
        if (utcSeconds == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(utcSeconds), java.time.ZoneOffset.UTC);
    }
}
