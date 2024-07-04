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

import de.sovity.edc.extension.contactcancellation.ContractAgreementTerminationDetails;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

// TODO find a way to switch these 2 tests from DB/sql style to EDC setup style for better test accuracy in the long term
class ContractContractAgreementTerminationDetailsQueryTest {

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
                ContractContractAgreementTerminationDetailsQueryTest.class
                    .getResource("/sql/AgreementTerminationDetailsQueryTest/init.sql").openStream().readAllBytes()
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
                val query = new ContractAgreementTerminationDetailsQuery(trx::dsl);

                // act
                val agreementId = "ZGVmMQ==:YXNzZXQx:YTg4N2U4YmMtODBjZS00OWI2LTk2MWEtMWU3Njc0NmM5N2Fi";
                val details = query.fetchAgreementDetails(agreementId);

                // assert
                assertThat(details).isPresent();
                // TODO: this is probably duplicating elements between provider/consumer/counterparty/negotiation type
                assertThat(details.get()).isEqualTo(new ContractAgreementTerminationDetails(
                    agreementId,
                    "my-edc",
                    "http://edc:11003/api/dsp",
                    ContractNegotiationStates.FINALIZED,
                    ContractNegotiation.Type.CONSUMER,
                    "my-edc",
                    "my-edc2",
                    null,
                    null,
                    null
                ));
            }
        );
    }

    @Test
    void fetchAgreementDetails_whenAgreementIsMissing_shouldReturnEmptyOptional() {
        // arrange

        dsl.transaction(trx ->
            {
                val query = new ContractAgreementTerminationDetailsQuery(trx::dsl);

                // act
                val details = query.fetchAgreementDetails("agreement:doesnt:exist");

                // assert
                assertThat(details).isEmpty();
            }
        );
    }

    @Test
    void fetchAgreementDetails_whenTerminationAlreadyExists_shouldReturnOptionalWithTerminationData() {
        // arrange

        dsl.transaction(trx ->
            {
                val query = new ContractAgreementTerminationDetailsQuery(trx::dsl);

                // act
                val agreementId = "Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NWM4M2MzNTYtZGVlYi00NjFkLTg1ZTUtODQ0YzgwMGEwMmVm";
                val maybeDetails = query.fetchAgreementDetails(agreementId);

                // assert
                assertThat(maybeDetails).isPresent();
                val details = maybeDetails.get();

                assertThat(details.contractAgreementId()).isEqualTo(agreementId);
                assertThat(details.counterpartyId()).isEqualTo("my-edc");
                assertThat(details.counterpartyAddress()).isEqualTo("http://edc:11003/api/dsp");
                assertThat(details.state()).isEqualTo(ContractNegotiationStates.FINALIZED);
                assertThat(details.type()).isEqualTo(ContractNegotiation.Type.CONSUMER);
                assertThat(details.providerAgentId()).isEqualTo("my-edc");
                assertThat(details.consumerAgentId()).isEqualTo("my-edc2");
                assertThat(details.reason()).isEqualTo("User Termination");
                assertThat(details.detail()).isEqualTo("Cancelled because of good reasons");
                assertThat(details.terminatedAt()).isEqualTo(OffsetDateTime.of(2024, 7, 3, 16, 59, 1, 518000000, ZoneOffset.UTC));
            }
        );
    }
}
