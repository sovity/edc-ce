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
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationDetails;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;


class ContractContractAgreementTerminationDetailsQueryTest {

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
    void fetchAgreementDetails_whenAgreementIsPresent_shouldReturnTheAgreementDetails(DSLContext dsl) {
        // arrange

        dsl.transaction(trx ->
            {
                dsl.execute(
                    new String(
                        ContractContractAgreementTerminationDetailsQueryTest.class
                            .getResource("/sql/AgreementTerminationDetailsQueryTest/init.sql").openStream().readAllBytes()
                    ));

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
                    null,
                    null
                ));
            }
        );
    }

    @Test
    void fetchAgreementDetails_whenAgreementIsMissing_shouldReturnEmptyOptional(DSLContext dsl) {
        // arrange

        dsl.transaction(trx ->
            {
                dsl.execute(
                    new String(
                        ContractContractAgreementTerminationDetailsQueryTest.class
                            .getResource("/sql/AgreementTerminationDetailsQueryTest/init.sql").openStream().readAllBytes()
                    ));

                val query = new ContractAgreementTerminationDetailsQuery(trx::dsl);

                // act
                val details = query.fetchAgreementDetails("agreement:doesnt:exist");

                // assert
                assertThat(details).isEmpty();
            }
        );
    }

    @Test
    void fetchAgreementDetails_whenTerminationAlreadyExists_shouldReturnOptionalWithTerminationData(DSLContext dsl) {
        // arrange

        dsl.transaction(trx ->
            {
                dsl.execute(
                    new String(
                        ContractContractAgreementTerminationDetailsQueryTest.class
                            .getResource("/sql/AgreementTerminationDetailsQueryTest/init.sql").openStream().readAllBytes()
                    ));

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
                assertThat(details.detail()).isEqualTo("Terminated because of good reasons");
                assertThat(details.terminatedAt()).isEqualTo(OffsetDateTime.of(2024, 7, 3, 16, 59, 1, 518000000, ZoneOffset.UTC));
                assertThat(details.terminatedBy()).isEqualTo(ContractTerminatedBy.SELF);
            }
        );
    }
}
