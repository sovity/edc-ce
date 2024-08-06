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

import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import kotlin.Pair;
import lombok.SneakyThrows;
import lombok.val;
import org.awaitility.core.ConditionTimeoutException;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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
        @Consumer DSLContext consumerDsl,
        @Provider DSLContext providerDsl
    ) {
        val output = setup(consumerDsl, providerDsl);

        writeBoth(consumerDsl, providerDsl, output, "started.csv");

        Stream.iterate(0, i -> i + 1)
            .limit(10)
            .map(i -> {
                val assetId = scenario.createAsset();
                scenario.createContractDefinition(assetId);
                val neg = scenario.negotiateAssetAndAwait(assetId);

                scenario.terminateContractAgreementAndAwait(
                    ContractNegotiation.Type.CONSUMER,
                    neg.getContractAgreementId(),
                    ContractTerminationRequest.builder()
                        .reason("test")
                        .detail("detail")
                        .build());

                try {
                    scenario.transferToMockServerAndAwait(neg.getContractAgreementId());
                } catch (ConditionTimeoutException e) {
                    // expected, ignored
                }

                return new Pair<>(assetId, neg);
            });

        val leaseCount1 = countLeases(consumerDsl);

        writeBoth(consumerDsl, providerDsl, output, "negotiated.csv");

        Thread.sleep(MILLISECONDS.convert(ofMinutes(1)));

        val leaseCount3 = countLeases(consumerDsl);

        writeBoth(consumerDsl, providerDsl, output, "ended.csv");

        System.out.println("JSONs " + output.toAbsolutePath());
        System.out.println("LEASES: " + leaseCount1 + ", " + leaseCount3);
    }

    private int countLeases(DSLContext consumerDsl) {
        return consumerDsl.selectCount().from(Tables.EDC_LEASE).fetchSingle().get(0, Integer.class);
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
