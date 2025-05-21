/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL
import kotlin.reflect.KProperty1

object JooqUtilsSovity {
    /**
     * Select a field "as" the property name of the given Kotlin Class (Infix Style)
     */
    infix fun <A, T> KProperty1<A, T>.from(f: () -> Field<T>): Field<T> =
        f().`as`(this.name)

    inline fun <reified T> Field<T>.eqAny(values: Collection<T>): Condition =
        this.eq(DSL.any(*values.toTypedArray()))

    inline fun <reified T> Field<T>.neAny(values: Collection<T>): Condition =
        this.ne(DSL.any(*values.toTypedArray()))
}
