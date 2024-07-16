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

package de.sovity.edc.extension.contacttermination.query;

import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationDetails;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;


class ContractAgreementTerminationDetailsQueryTest {

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "edc",
        testDatabase -> {
            val config = forTestDatabase("my-edc-participant-id", testDatabase);
            return config.getProperties();
        }
    );

    @Test
    void fetchAgreementDetailsOrThrow_whenAgreementIsPresent_shouldReturnTheAgreementDetails(DSLContext dsl) {
        // arrange

        dsl.transaction(trx -> {
                setup(trx.dsl());

                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = "ZGVmMQ==:YXNzZXQx:YTg4N2U4YmMtODBjZS00OWI2LTk2MWEtMWU3Njc0NmM5N2Fi";
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                // assert
                assertThat(details).isEqualTo(ContractAgreementTerminationDetails.builder()
                    .contractAgreementId(agreementId)
                    .counterpartyId("my-edc")
                    .counterpartyAddress("http://edc:11003/api/dsp")
                    .type(ContractNegotiation.Type.CONSUMER)
                    .providerAgentId("my-edc")
                    .consumerAgentId("my-edc2")
                    .reason(null)
                    .detail(null)
                    .terminatedAt(null)
                    .terminatedBy(null)
                    .build());
            }
        );
    }

    private static void setup(DSLContext dsl) throws IOException {
        dsl.execute(
            new String(
                ContractAgreementTerminationDetailsQueryTest.class
                    .getResource("/sql/AgreementTerminationDetailsQueryTest/init.sql").openStream().readAllBytes()
            ));
    }

    @Test
    void fetchAgreementDetailsOrThrow_whenAgreementIsMissing_shouldReturnEmptyOptional(DSLContext dsl) {
        // arrange

        dsl.transaction(trx -> {
                setup(trx.dsl());

                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), "agreement:doesnt:exist");

                // assert
                assertThat(details).isNull();
            }
        );
    }

    @Test
    void fetchAgreementDetailsOrThrow_whenTerminationAlreadyExists_shouldReturnOptionalWithTerminationData(DSLContext dsl) {
        // arrange

        dsl.transaction(trx -> {
                setup(trx.dsl());

                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = "Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NWM4M2MzNTYtZGVlYi00NjFkLTg1ZTUtODQ0YzgwMGEwMmVm";
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                // assert
                assertThat(details.contractAgreementId()).isEqualTo(agreementId);
                assertThat(details.counterpartyId()).isEqualTo("my-edc");
                assertThat(details.counterpartyAddress()).isEqualTo("http://edc:11003/api/dsp");
                assertThat(details.type()).isEqualTo(ContractNegotiation.Type.CONSUMER);
                assertThat(details.providerAgentId()).isEqualTo("my-edc");
                assertThat(details.consumerAgentId()).isEqualTo("my-edc2");
                assertThat(details.reason()).isEqualTo("User Termination");
                assertThat(details.detail()).isEqualTo("Terminated because of good reasons");
                assertThat(details.terminatedAt()).isEqualTo(OffsetDateTime.of(2024, 7, 3, 16, 59, 1, 518000000, ZoneOffset.UTC));
                assertThat(details.terminatedBy()).isEqualTo(ContractTerminatedBy.SELF);
            }
        );
    }
}
