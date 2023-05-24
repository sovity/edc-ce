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

package de.sovity.edc.ext.brokerserver.dao.queries.utils;

import de.sovity.edc.ext.brokerserver.utils.StringUtils2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * DB Search Queries
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchUtils {

    /**
     * Simple search
     * <br>
     * All search query words must be contained in at least one search target.
     *
     * @param searchQuery   search query
     * @param searchTargets target fields
     * @return JOOQ Condition
     */
    public static Condition simpleSearch(String searchQuery, List<Field<String>> searchTargets) {
        var words = StringUtils2.lowercaseWords(searchQuery);
        return DSL.and(words.stream()
                .map(word -> anySearchTargetContains(searchTargets, word))
                .toList());
    }

    private static Condition anySearchTargetContains(List<Field<String>> searchTargets, String word) {
        return DSL.or(searchTargets.stream().map(field -> LikeUtils.contains(field, word)).toList());
    }
}
