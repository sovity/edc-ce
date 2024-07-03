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
 */

package de.sovity.edc.extension.contactcancellation.query;

import de.sovity.edc.utils.versions.GradleVersions;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

class AgreementDetailsQueryTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(GradleVersions.POSTGRES_IMAGE_TAG);
    private static DSLContext dsl;

    @BeforeAll
    @SneakyThrows
    public static void beforeAll() {

        POSTGRES.start();

        val migrator = Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .load();
        migrator.migrate();

        // TODO: cleanup and put this in a reusable class/wrapper around the testcontainers PG DB
        val connection = POSTGRES.createConnection("?");
        dsl = DSL.using(connection, SQLDialect.POSTGRES);
        dsl.execute(
            new String(
                AgreementDetailsQueryTest.class
                    .getResource("/sql/CancellationRequestValidationQueryTest/fetchAgreementDetails_whenAgreementIsPresent_shouldReturnTheAgreementDetails.init.sql").openStream().readAllBytes()
            ));
    }

    @AfterAll
    public static void afterAll() {
        POSTGRES.stop();
    }

    @SneakyThrows
    @Test
    void fetchAgreementDetails_whenAgreementIsPresent_shouldReturnTheAgreementDetails() {
        // arrange

        dsl.transaction(trx ->
            {
                val query = new AgreementDetailsQuery(trx.dsl());

                // act
                val details = query.fetchAgreementDetails("ZGVmMQ==:YXNzZXQx:YTg4N2U4YmMtODBjZS00OWI2LTk2MWEtMWU3Njc0NmM5N2Fi");

                // assert
                assertThat(details).isPresent();
                // TODO: this is probably duplicating elements between provider/consumer/counterparty/negotiation type
                assertThat(details.get()).isEqualTo(new AgreementDetails(
                    "my-edc",
                    "http://edc:11003/api/dsp",
                    ContractNegotiationStates.FINALIZED,
                    "my-edc",
                    "my-edc2",
                    ContractNegotiation.Type.CONSUMER
                ));
            }
        );
    }

    @Test
    void fetchAgreementDetails_whenAgreementIsMissing_shouldReturnEmptyOptional() {
        // arrange

        dsl.transaction(trx ->
            {
                val query = new AgreementDetailsQuery(trx.dsl());

                // act
                val details = query.fetchAgreementDetails("ente:ente:ente");

                // assert
                assertThat(details).isEmpty();
            }
        );
    }
}
