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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils2 {
    /**
     * Set Difference
     *
     * @param a   base set
     * @param b   remove these items
     * @param <T> set item type
     * @return a difference b
     */
    public static <T> Set<T> difference(@NonNull Collection<T> a, @NonNull Collection<T> b) {
        var result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <T> List<T> allElementsExceptForIndex(Collection<T> source, int skipIndex) {
        var result = new ArrayList<>(source);
        result.remove(skipIndex);
        return result;
    }
}
