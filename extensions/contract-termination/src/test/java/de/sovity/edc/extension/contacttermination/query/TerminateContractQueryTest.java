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

import de.sovity.edc.extension.contacttermination.ContractTerminationParam;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;

class TerminateContractQueryTest {

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
    void terminateConsumerAgreementOrThrow_shouldInsertRowInTerminationTable(DSLContext dsl) {
        dsl.transaction(trx -> {
            dsl.execute(
                new String(
                    ContractAgreementTerminationDetailsQueryTest.class
                        .getResource("/sql/TerminateContractQueryTest/init.sql").openStream().readAllBytes()
                ));

                // arrange
                val query = new TerminateContractQuery();
                val agreementId = "Y29udHJhY3QtMjIwMDA=:YXNzZXQtMjIwMDA=:NzYzYzkxODctZTQ3Yi00ODJjLTkxMjAtYTJkMTM1MzQ2YWVm";

                val details = new ContractTerminationParam(
                    agreementId,
                    "Some detail",
                    "Some reason"
                );
                val now = OffsetDateTime.now();

                // act
                val terminatedAt = query.terminateConsumerAgreementOrThrow(trx.dsl(), details, COUNTERPARTY);

                // assert
                assertThat(terminatedAt).isNotNull();

                val detailsQuery = new ContractAgreementTerminationDetailsQuery();
                val detailsAfterTermination = detailsQuery.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                assertThat(detailsAfterTermination.contractAgreementId()).isEqualTo(agreementId);
                assertThat(detailsAfterTermination.counterpartyId()).isEqualTo("my-edc2");
                assertThat(detailsAfterTermination.counterpartyAddress()).isEqualTo("http://edc2:11003/api/dsp");
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
