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
import lombok.SneakyThrows;
import lombok.val;
import org.awaitility.core.ConditionTimeoutException;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.stream.Stream;


public class DbPerformanceTest {

    private static final int wait = 1000;

    @RegisterExtension
    private static final E2eTestExtension EXTENSION = new E2eTestExtension(
        CeE2eTestExtensionConfigFactory.defaultBuilder().toBuilder()
            .consumerConfigCustomizer(config -> {
                config.setProperty("edc.transfer.state-machine.iteration-wait-millis", "" + wait);
                config.setProperty("edc.negotiation.state-machine.iteration-wait-millis", "" + wait);
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
        val duration = Duration.ofMinutes(5);
        val start = System.currentTimeMillis();

        val output = setup(consumerDsl, providerDsl);

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val neg = scenario.negotiateAssetAndAwait(assetId);

        val leaseCount1 = countLeases(consumerDsl);

        scenario.terminateContractAgreementAndAwait(
            ContractNegotiation.Type.CONSUMER,
            neg.getContractAgreementId(),
            ContractTerminationRequest.builder()
                .reason("test")
                .detail("detail")
                .build());

        int MAX_LEASES_COUNT = 128;

        Stream.iterate(0, i -> i + 1)
            .parallel()
            .limit(MAX_LEASES_COUNT)
            .forEach(it -> {
                try {
                    System.out.println("Iteration " + it);
                    scenario.transferToMockServerAndAwait(neg.getContractAgreementId());
                } catch (ConditionTimeoutException e) {
                    // expected, ignored
                }
            });

        val leaseCount2 = countLeases(consumerDsl);

        val uuidVersion = getUuidVersion();

        writeBoth(consumerDsl, providerDsl, output, uuidVersion, MAX_LEASES_COUNT, 0, duration, wait, "negotiated.csv");

        val durationSinceStart = System.currentTimeMillis() - start;
        Thread.sleep(duration.toMillis() - durationSinceStart);

        val leaseCount3 = countLeases(consumerDsl);

        val out = writeBoth(consumerDsl, providerDsl, output, uuidVersion, MAX_LEASES_COUNT, 0, duration, wait, "ended.csv");

        System.out.println("JSONs " + out);
        System.out.println("LEASES: " + leaseCount1 + ", " + leaseCount2 + ", " + leaseCount3);
    }

    @Test
    void uuidVersion() {
        val uuidVersion = getUuidVersion();

        System.out.println(uuidVersion);
    }

    private static @NotNull String getUuidVersion() {
        String uuidVersion;
        try {
            Class.forName("com.github.f4b6a3.uuid.UuidCreator");
            uuidVersion = "7";
        } catch (Exception e) {
            uuidVersion = "4";
        }
        return uuidVersion;
    }

    private int countLeases(DSLContext consumerDsl) {
        return consumerDsl.selectCount().from(Tables.EDC_LEASE).fetchSingle().get(0, Integer.class);
    }

    private static String writeBoth(
        DSLContext consumerDsl,
        DSLContext providerDsl,
        Path outputBase,
        String uuidVersion,
        int leasesCount,
        int runIndex,
        Duration duration,
        int wait,
        String filename
    ) throws IOException {
        val output = outputBase.resolve("uuid")
            .resolve(uuidVersion)
            .resolve("leases")
            .resolve(String.valueOf(leasesCount))
            .resolve("duration")
            .resolve(String.valueOf(duration.toMillis()))
            .resolve("wait")
            .resolve("" + wait)
            .resolve("run")
            .resolve(String.valueOf(runIndex));

        writeRequests(providerDsl, output.resolve("provider").resolve(filename));
        writeRequests(consumerDsl, output.resolve("consumer").resolve(filename));

        return output.toAbsolutePath().toString();
    }

    @SneakyThrows
    private Path setup(DSLContext consumerDsl, DSLContext providerDsl) {
        consumerDsl.resultQuery("create extension pg_stat_statements").execute();
        providerDsl.resultQuery("create extension pg_stat_statements").execute();

        return Paths.get("/home/uuh/.config/JetBrains/IntelliJIdea2024.1/scratches/pmo-infra/167/data");
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
