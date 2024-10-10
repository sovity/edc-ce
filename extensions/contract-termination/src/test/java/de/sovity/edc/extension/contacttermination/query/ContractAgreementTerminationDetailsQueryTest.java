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
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.junit.multi.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.multi.annotations.Consumer;
import de.sovity.edc.extension.e2e.junit.multi.annotations.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.config.ConfigUtils;
import lombok.val;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation.Type.CONSUMER;

class ContractAgreementTerminationDetailsQueryTest {

    @RegisterExtension
    private static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @DisabledOnGithub
    @Test
    void fetchAgreementDetailsOrThrow_whenAgreementIsPresent_shouldReturnTheAgreementDetails(
        E2eTestScenario scenario,
        @Consumer DslContextFactory dslContextFactory,
        @Provider Config providerConfig
    ) {
        // arrange

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiations = scenario.negotiateAssetAndAwait(assetId);

        dslContextFactory.rollbackTransaction(dsl -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = negotiations.getContractAgreementId();
                val details = query.fetchAgreementDetailsOrThrow(dsl, agreementId);

                // assert
                assertThat(details).isEqualTo(ContractAgreementTerminationDetails.builder()
                    .contractAgreementId(agreementId)
                    .counterpartyId("provider")
                    .counterpartyAddress(ConfigUtils.getProtocolApiUrl(providerConfig))
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
    void fetchAgreementDetailsOrThrow_whenAgreementIsMissing_shouldReturnEmptyOptional(@Consumer DslContextFactory dslContextFactory) {
        // arrange

        dslContextFactory.rollbackTransaction(dsl -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val details = query.fetchAgreementDetailsOrThrow(dsl, "agreement:doesnt:exist");

                // assert
                assertThat(details).isNull();
            }
        );
    }

    @Test
    void fetchAgreementDetailsOrThrow_whenTerminationAlreadyExists_shouldReturnOptionalWithTerminationData(
        E2eTestScenario scenario,
        @Consumer DslContextFactory dslContextFactory,
        @Provider Config providerConfig
    ) {
        // arrange

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiations = scenario.negotiateAssetAndAwait(assetId);
        val terminationRequest = new ContractTerminationRequest("Terminated because of good reasons", "User Termination");
        val termination = scenario.terminateContractAgreementAndAwait(CONSUMER, negotiations.getContractAgreementId(), terminationRequest);

        dslContextFactory.rollbackTransaction(dsl -> {
                val query = new ContractAgreementTerminationDetailsQuery();

                // act
                val agreementId = negotiations.getContractAgreementId();
                val details = query.fetchAgreementDetailsOrThrow(dsl, agreementId);

                // assert
                assertThat(details.contractAgreementId()).isEqualTo(agreementId);
                assertThat(details.counterpartyId()).isEqualTo("provider");
                assertThat(details.counterpartyAddress()).isEqualTo(ConfigUtils.getProtocolApiUrl(providerConfig));
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
