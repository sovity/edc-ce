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
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import lombok.SneakyThrows;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.time.Duration.ofMinutes;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class DbPerformanceTest {

    @RegisterExtension
    private static final E2eTestExtension EXTENSION = new E2eTestExtension(
        CeE2eTestExtensionConfigFactory.defaultBuilder().toBuilder()
            .consumerConfigCustomizer(config -> {
                config.setProperty("edc.transfer.state-machine.iteration-wait-millis", "2000");
                config.setProperty("edc.negotiation.state-machine.iteration-wait-millis", "2000");
            })
            .build()
    );

    @SneakyThrows
    @Test
    void whatsHappeningWithAllThoseDbQueries(
        E2eScenario scenario,
        ClientAndServer clientAndServer,
        @Consumer DSLContext consumerDsl,
        @Provider DSLContext providerDsl
    ) {
        val output = setup(consumerDsl, providerDsl);

        writeBoth(consumerDsl, providerDsl, output, "started.csv");

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val neg = scenario.negotiateAssetAndAwait(assetId);

        scenario.transferToMockServerAndAwait(neg.getContractAgreementId());

        writeBoth(consumerDsl, providerDsl, output, "negotiated.csv");

        Thread.sleep(MILLISECONDS.convert(ofMinutes(1)));

        writeBoth(consumerDsl, providerDsl, output, "ended.csv");

        System.out.println("JSONs " + output.toAbsolutePath());
    }

    private static void writeBoth(DSLContext consumerDsl, DSLContext providerDsl, Path output, String other) throws IOException {
        writeRequests(providerDsl, output.resolve("provider").resolve(other));
        writeRequests(consumerDsl, output.resolve("consumer").resolve(other));
    }

    @SneakyThrows
    private Path setup(DSLContext consumerDsl, DSLContext providerDsl) {
        consumerDsl.resultQuery("create extension pg_stat_statements").execute();
        providerDsl.resultQuery("create extension pg_stat_statements").execute();

        return Files.createTempDirectory("db").resolve("run0");
    }

    private static void writeRequests(DSLContext dsl, Path output) throws IOException {
        val q = """
            SELECT
                query AS query,
                total_exec_time,
                calls,
                mean_exec_time
            FROM pg_stat_statements
            ORDER BY calls
            """;

        val result = dsl.resultQuery(q).fetch();

        output.toFile().getParentFile().mkdirs();
        try (val writer = new FileWriter(output.toFile())) {
            val format = JSONFormat.DEFAULT_FOR_RESULTS
                .header(false)
                .recordFormat(JSONFormat.RecordFormat.OBJECT);
            val json = result.formatJSON(format);
            writer.write(json);
        }
    }
}
