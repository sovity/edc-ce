/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.ce.utils.escapeForSqlLike
import org.apache.commons.lang3.StringUtils
import org.jooq.Condition
import org.jooq.Field
import org.jooq.JSON
import org.jooq.impl.DSL
import kotlin.reflect.KProperty1

object JooqUtilsSovity {
    private val objectMapper = ObjectMapper()

    /**
     * Select a field "as" the property name of the given Kotlin Class (Infix Style)
     */
    infix fun <A, T> KProperty1<A, T>.from(f: () -> Field<T>): Field<T> =
        f().`as`(this.name)

    infix fun <A, T> KProperty1<A, List<T>>.fromArray(f: () -> Field<Array<T>>): Field<Array<T>> =
        f().`as`(this.name)

    inline fun <reified T> Field<T>.eqAny(values: Collection<T>): Condition =
        this.eq(DSL.any(*values.toTypedArray()))

    inline fun <reified T> Field<T>.neAny(values: Collection<T>): Condition =
        this.ne(DSL.any(*values.toTypedArray()))

    fun Field<String?>.containsString(lowercaseWord: String): Condition {
        if (StringUtils.isBlank(lowercaseWord)) {
            return DSL.trueCondition()
        }

        return this.likeIgnoreCase("%" + lowercaseWord.escapeForSqlLike() + "%")
    }

    fun JSON.parseStringArray(): List<String> =
        objectMapper.readValue(this.data(), object : TypeReference<List<String>>() {})

    fun List<String>.toPostgresqlJsonArray(): JSON = JSON.json(objectMapper.writeValueAsString(this))
}
