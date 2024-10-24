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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractAgreementPage;
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractTerminatedBy;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.awaitility.Awaitility;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@UtilityClass
public class ContractTerminationTestUtils {

    public static void awaitTerminationCount(EdcClient client, int count) {
        Awaitility.await().atMost(ofSeconds(5)).until(
            () -> client.uiApi()
                .getContractAgreementPage(ContractAgreementPageQuery.builder().build())
                .getContractAgreements()
                .stream()
                .filter(it -> it.getTerminationStatus().equals(TERMINATED))
                .count() >= count
        );
    }

    public static void assertTermination(
        ContractAgreementPage consumerSideAgreements,
        String detail,
        String reason,
        ContractTerminatedBy terminatedBy
    ) {
        val contractAgreements = consumerSideAgreements.getContractAgreements();
        assertThat(contractAgreements).hasSize(1);
        assertThat(contractAgreements.get(0).getTerminationStatus()).isEqualTo(TERMINATED);

        val consumerInformation = contractAgreements.get(0).getTerminationInformation();

        assertThat(consumerInformation).isNotNull();

        val now = OffsetDateTime.now();
        assertThat(consumerInformation.getTerminatedAt()).isCloseTo(now, within(1, ChronoUnit.MINUTES));

        assertThat(consumerInformation.getDetail()).isEqualTo(detail);
        assertThat(consumerInformation.getReason()).isEqualTo(reason);
        assertThat(consumerInformation.getTerminatedBy()).isEqualTo(terminatedBy);
    }

}
