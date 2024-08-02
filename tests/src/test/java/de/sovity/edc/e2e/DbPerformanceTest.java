/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.e2e;

import de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


public class DbPerformanceTest {

    @RegisterExtension
    private static final E2eTestExtension EXTENSION = new E2eTestExtension(
        CeE2eTestExtensionConfigFactory.defaultBuilder()
    );

    @Test
    void whatsHappeningWithAllThoseDbQueries(
        @Consumer DSLContext consumerDsl,
        @Provider DSLContext providerDsl
    ) {

        consumerDsl.resultQuery("create extension pg_stat_statements").execute();

        val q = """
            SELECT
                query AS query,
                total_exec_time,
                calls,
                mean_exec_time
            FROM    pg_stat_statements
            ORDER BY total_exec_time DESC
            LIMIT 10
            """;

        val result = consumerDsl.resultQuery(q).fetch();

        result.forEach(row -> {
            System.out.println("Query: " + row.get("query"));
            System.out.println("Total exec time: " + row.get("total_exec_time"));
            System.out.println("Calls: " + row.get("calls"));
            System.out.println("Mean exec time: " + row.get("mean_exec_time"));
            System.out.println();
        });
    }
}
