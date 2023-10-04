/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.ext.wrapper.TestUtils;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class KpiApiTest {
    EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }
    @Test
    void getKpis() {
        // act
        var actual = client.useCaseApi().getKpis();

        // assert
        assertThat(actual.getAssetsCount()).isZero();
        assertThat(actual.getContractAgreementsCount()).isZero();
        assertThat(actual.getContractDefinitionsCount()).isZero();
        assertThat(actual.getPoliciesCount()).isEqualTo(1);
        assertThat(actual.getTransferProcessDto().getIncomingTransferProcessCounts()).isEmpty();
        assertThat(actual.getTransferProcessDto().getOutgoingTransferProcessCounts()).isEmpty();
        assertThat(actual.getAssetsCount()).isZero();
    }
}
