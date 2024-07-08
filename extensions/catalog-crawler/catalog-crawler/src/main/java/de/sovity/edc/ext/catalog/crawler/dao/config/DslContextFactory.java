package de.sovity.edc.ext.catalog.crawler.dao.config;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;

/**
 * Quickly launch {@link org.jooq.DSLContext}s from EDC configuration.
 */
@RequiredArgsConstructor
public class DslContextFactory {
    private final DataSource dataSource;

    /**
     * Create new {@link DSLContext} for querying DB.
     *
     * @return new {@link DSLContext}
     */
    public DSLContext newDslContext() {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    /**
     * Utility method for when the {@link DSLContext} will be used only for a single transaction.
     * <br>
     * An example would be a REST request.
     *
     * @param <R> return type
     * @return new {@link DSLContext} + opened transaction
     */
    public <R> R transactionResult(Function<DSLContext, R> function) {
        return newDslContext().transactionResult(transaction -> function.apply(transaction.dsl()));
    }

    /**
     * Utility method for when the {@link DSLContext} will be used only for a single transaction.
     * <br>
     * An example would be a REST request.
     */
    public void transaction(Consumer<DSLContext> function) {
        newDslContext().transaction(transaction -> function.accept(transaction.dsl()));
    }

    /**
     * Runs given code within a test transaction.
     *
     * @param code code to run within the test transaction
     */
    public void testTransaction(Consumer<DSLContext> code) {
        try {
            transaction(dsl -> {
                code.accept(dsl);
                throw new TestTransactionNoopException();
            });
        } catch (TestTransactionNoopException e) {
            // Ignore
        }
    }

    private static class TestTransactionNoopException extends RuntimeException {
    }
}
