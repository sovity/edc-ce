package de.sovity.edc.ext.brokerserver.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jooq.DSLContext;

/**
 * Hijack all {@link DslContextFactory}s from test code (single thread only)
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DslContextFactoryHijacker {
    @Getter
    private static DSLContext parentDslContext = null;

    /**
     * Our tests currently have no access to the running extension's context, save for REST calls.
     * <br>
     * We use this class to hack all DslContextFactories via {@link #parentDslContext}.
     * <br>
     * If we set the {@link #parentDslContext} to one we created with a transaction we won't commit, we won't have to reset the DB between tests.
     *
     * @param testTransactionDslContext parent dsl context containing the parent transaction
     * @param r                         code to run
     */
    public static void withParentDslContext(DSLContext testTransactionDslContext, Runnable r) {
        if (parentDslContext != null) {
            throw new IllegalStateException("Tests are being run in parallel, which won't work with our current architecture.");
        }

        DslContextFactoryHijacker.parentDslContext = testTransactionDslContext;

        try {
            r.run();
        } finally {
            DslContextFactoryHijacker.parentDslContext = null;
        }
    }
}
