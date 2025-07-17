/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.containsString
import de.sovity.edc.ce.utils.lowercaseWords
import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

object SearchUtils {
    fun simpleSearch(searchQuery: String?, searchTargets: List<Field<String?>>): Condition {
        val words = searchQuery.lowercaseWords()
        return DSL.and(words.map { anySearchTargetContains(searchTargets, it) })
    }

    private fun anySearchTargetContains(searchTargets: List<Field<String?>>, word: String): Condition =
        DSL.or(searchTargets.map { it.containsString(word) })
}
