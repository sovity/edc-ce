/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.db.directaccess;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;

@ExtensionPoint
@RequiredArgsConstructor
public class DslContextFactory {

    private final DataSource dataSource;

    private DSLContext newDslContext() {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    public void transaction(Consumer<DSLContext> consumer) {
        newDslContext().transaction(trx -> consumer.accept(trx.dsl()));
    }

    /**
     * For test purposes: a transaction that never succeeds
     */
    public void rollbackTransaction(Consumer<DSLContext> consumer) {
        try {
            newDslContext().transaction((trx) -> {
                consumer.accept(trx.dsl());
                throw new RollbackException();
            });
        } catch (RollbackException e) {
            // swallowed. Expected.
        }
    }

    public <T> T transactionResult(Function<DSLContext, T> f) {
        return newDslContext().transactionResult(trx -> f.apply(trx.dsl()));
    }
}
