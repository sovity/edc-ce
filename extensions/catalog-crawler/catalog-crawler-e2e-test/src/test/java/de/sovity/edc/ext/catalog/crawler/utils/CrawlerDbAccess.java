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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.utils;

import de.sovity.edc.extension.e2e.db.TestDatabase;
import lombok.experimental.UtilityClass;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Consumer;

@UtilityClass
public class CrawlerDbAccess {

    public static void transaction(TestDatabase testDatabase, Consumer<DSLContext> consumer) {
        var credentials = testDatabase.getJdbcCredentials();
        try (var dslContext = DSL.using(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword())) {
            dslContext.transaction(configuration -> {
                var dsl = DSL.using(configuration);
                consumer.accept(dsl);
            });
        }
    }
}
