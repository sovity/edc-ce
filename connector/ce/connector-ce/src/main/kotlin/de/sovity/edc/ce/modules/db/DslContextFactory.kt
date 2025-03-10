/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import lombok.RequiredArgsConstructor
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.TransactionContext
import org.jooq.TransactionProvider
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import java.util.function.Consumer
import javax.sql.DataSource

@RequiredArgsConstructor
class DslContextFactory(
    private val dataSource: DataSource,
    private val transactionContext: org.eclipse.edc.transaction.spi.TransactionContext
) {

    private fun newDslContext(): DSLContext {
        val configuration = DefaultConfiguration()
        configuration.setDataSource(dataSource)
        configuration.setSQLDialect(SQLDialect.POSTGRES)
        configuration.setTransactionProvider(PleaseUseEdcTransactionHandling)
        return DSL.using(configuration)
    }

    fun transaction(
        // Consumer used Java-Style to avoid Java code having to return "Unit.INSTANCE"
        // TODO: Once Java code is Kotlin, change to (dsl: DSLContext) -> Unit
        fn: Consumer<DSLContext>
    ) {
        transactionContext.execute {
            val dsl = newDslContext()
            fn.accept(dsl)
        }
    }

    fun <T> transactionResult(fn: (dsl: DSLContext) -> T): T {
        return transactionContext.execute<T> {
            val dsl = newDslContext()
            fn(dsl)
        }
    }

    /**
     * For test purposes: a transaction that won't be committed
     */
    fun rollbackTransaction(fn: Consumer<DSLContext>) {
        try {
            transactionContext.execute {
                val dsl = newDslContext()
                fn.accept(dsl)
                throw RollbackException()
            }
        } catch (_: RollbackException) {
            // swallowed. Expected.
        }
    }

    private object PleaseUseEdcTransactionHandling : TransactionProvider {
        override fun begin(p0: TransactionContext) {
            unsupported()
        }

        override fun commit(p0: TransactionContext) {
            unsupported()
        }

        override fun rollback(p0: TransactionContext) {
            unsupported()
        }

        /**
         * Unsupported, please use EDC transaction handling.
         */
        private fun unsupported(): Nothing {
            @Suppress("MaxLineLength")
            error("Please use ${org.eclipse.edc.transaction.spi.TransactionContext::class.java.name} or ${DslContextFactory::class.java.name} for transactions. Do not use jOOQ's TransactionProvider, the transaction is controlled via the EDC manipulating the backing connection.")
        }
    }
}
