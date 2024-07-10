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

package de.sovity.edc.extension.contacttermination.query;

import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import de.sovity.edc.extension.contacttermination.ContractTermination;
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

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static org.assertj.core.api.Assertions.assertThat;

class TerminateContractQueryTest {

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
                    .getResource("/sql/TerminateContractQueryTest/init.sql").openStream().readAllBytes()
            ));
    }

    @AfterAll
    public static void afterAll() {
        POSTGRES.stop();
    }

    @Test
    void terminateConsumerAgreement_shouldInsertRowInTerminationTable() {
        dsl.transaction(trx ->
            {
                // arrange
                val query = new TerminateContractQuery(trx::dsl);
                val agreementId = "Y29udHJhY3QtMjIwMDA=:YXNzZXQtMjIwMDA=:NzYzYzkxODctZTQ3Yi00ODJjLTkxMjAtYTJkMTM1MzQ2YWVm";

                val details = new ContractTermination(
                    agreementId,
                    "Some detail",
                    "Some reason"
                );
                val now = OffsetDateTime.now();

                // act
                val terminatedAt = query.terminateConsumerAgreement(details, COUNTERPARTY);

                // assert
                assertThat(terminatedAt).isNotNull();

                val detailsQuery = new ContractAgreementTerminationDetailsQuery(trx::dsl);
                val maybeDetailsAfterTermination = detailsQuery.fetchAgreementDetails(agreementId);
                assertThat(maybeDetailsAfterTermination).isPresent();
                val detailsAfterTermination = maybeDetailsAfterTermination.get();

                assertThat(detailsAfterTermination.contractAgreementId()).isEqualTo(agreementId);
                assertThat(detailsAfterTermination.counterpartyId()).isEqualTo("my-edc2");
                assertThat(detailsAfterTermination.counterpartyAddress()).isEqualTo("http://edc2:11003/api/dsp");
                assertThat(detailsAfterTermination.state()).isEqualTo(ContractNegotiationStates.FINALIZED);
                assertThat(detailsAfterTermination.type()).isEqualTo(ContractNegotiation.Type.CONSUMER);
                assertThat(detailsAfterTermination.providerAgentId()).isEqualTo("my-edc2");
                assertThat(detailsAfterTermination.consumerAgentId()).isEqualTo("my-edc");
                assertThat(detailsAfterTermination.reason()).isEqualTo("Some reason");
                assertThat(detailsAfterTermination.detail()).isEqualTo("Some detail");
                assertThat(detailsAfterTermination.terminatedAt()).isBetween(now, now.plusSeconds(1));
                assertThat(detailsAfterTermination.terminatedBy()).isEqualTo(COUNTERPARTY);
            }
        );
    }
}
