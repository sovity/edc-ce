/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import de.sovity.edc.ce.utils.escapeForSqlLike
import org.apache.commons.lang3.StringUtils
import org.jooq.Condition
import org.jooq.Field
import org.jooq.JSON
import org.jooq.TableLike
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import java.time.OffsetDateTime
import kotlin.reflect.KProperty1

object JooqUtilsSovity {
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

    @JvmStatic
    fun jsonField(jsonField: Field<JSON>, property: String) = DSL.field(
        "${jsonField.name} ->> '$property'",
        String::class.java
    )

    inline fun <reified R> multiset(table: TableLike<*>): Field<List<R>> =
        DSL.multiset(table).convertFrom { it.into(R::class.java) }

    fun Field<Long>.toOffsetDateTimeFromUtcSeconds(): Field<OffsetDateTime> {
        return DSL.field(
            "to_timestamp({0})", SQLDataType.OFFSETDATETIME,
            this
        )
    }
}
