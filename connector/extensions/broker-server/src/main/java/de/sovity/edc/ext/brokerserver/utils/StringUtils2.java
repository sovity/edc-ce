/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.brokerserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils2 {

    /**
     * Removes the suffix from the given string if it ends with it.
     *
     * @param string string
     * @param suffix suffix to remove
     * @return string without suffix
     */
    public static String removeSuffix(@NonNull String string, @NonNull String suffix) {
        if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

    /**
     * Splits a string into its words and returns them in lowercase.
     *
     * @param string string
     * @return list of lowercase words
     */
    public static List<String> lowercaseWords(String string) {
        if (StringUtils.isBlank(string)) {
            return List.of();
        }

        return Stream.of(string.split("\\s+"))
                .map(String::toLowerCase)
                .filter(StringUtils::isNotBlank)
                .toList();
    }
}
