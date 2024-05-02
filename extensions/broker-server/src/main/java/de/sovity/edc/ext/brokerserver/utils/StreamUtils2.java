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

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils2 {

    /**
     * Returns a predicate that filters out all elements that have the same key as a previous element.
     *
     * @param keyFn key extractor
     * @param <T>   item type
     * @param <K>   key type
     * @return predicate to be used in {@link java.util.stream.Stream#filter(Predicate)}
     */
    public static <T, K> Predicate<T> distinctByKey(Function<T, K> keyFn) {
        var keys = new HashSet<>();
        return t -> keys.add(keyFn.apply(t));
    }
}
