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

import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationDetails;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation.Type.CONSUMER;


@ExtendWith(E2eTestExtension.class)
class ContractAgreementTerminationDetailsQueryTest {

    @Test
    void fetchAgreementDetailsOrThrow_whenAgreementIsPresent_shouldReturnTheAgreementDetails(
        E2eScenario scenario,
        @Consumer DSLContext dsl,
        @Provider ConnectorConfig providerConfig
    ) {
        // arrange

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiations = scenario.negotiateAssetAndAwait(assetId);

        dsl.transaction(trx -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = negotiations.getContractAgreementId();
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                // assert
                assertThat(details).isEqualTo(ContractAgreementTerminationDetails.builder()
                    .contractAgreementId(agreementId)
                    .counterpartyId("provider")
                    .counterpartyAddress(providerConfig.getProtocolEndpoint().getUri().toString())
                    .type(CONSUMER)
                    .providerAgentId("provider")
                    .consumerAgentId("consumer")
                    .reason(null)
                    .detail(null)
                    .terminatedAt(null)
                    .terminatedBy(null)
                    .build());
            }
        );
    }

    @Test
    void fetchAgreementDetailsOrThrow_whenAgreementIsMissing_shouldReturnEmptyOptional(@Consumer DSLContext dsl) {
        // arrange

        dsl.transaction(trx -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), "agreement:doesnt:exist");

                // assert
                assertThat(details).isNull();
            }
        );
    }

    @Test
    void fetchAgreementDetailsOrThrow_whenTerminationAlreadyExists_shouldReturnOptionalWithTerminationData(
        E2eScenario scenario,
        @Consumer DSLContext dsl,
        @Provider ConnectorConfig providerConfig
    ) {
        // arrange

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiations = scenario.negotiateAssetAndAwait(assetId);
        val terminationRequest = new ContractTerminationRequest("Terminated because of good reasons", "User Termination");
        val termination = scenario.terminateAndAwait(CONSUMER, negotiations.getContractAgreementId(), terminationRequest);

        dsl.transaction(trx -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = negotiations.getContractAgreementId();
                val details = query.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                // assert
                assertThat(details.contractAgreementId()).isEqualTo(agreementId);
                assertThat(details.counterpartyId()).isEqualTo("provider");
                assertThat(details.counterpartyAddress()).isEqualTo(providerConfig.getProtocolEndpoint().getUri().toString());
                assertThat(details.type()).isEqualTo(CONSUMER);
                assertThat(details.providerAgentId()).isEqualTo("provider");
                assertThat(details.consumerAgentId()).isEqualTo("consumer");
                assertThat(details.reason()).isEqualTo("User Termination");
                assertThat(details.detail()).isEqualTo("Terminated because of good reasons");
                assertThat(details.terminatedAt()).isEqualTo(termination.getLastUpdatedDate());
                assertThat(details.terminatedBy()).isEqualTo(ContractTerminatedBy.SELF);
            }
        );
    }
}
