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
        var result = client.useCaseApi().getKpis();
        assertThat(result.getAssetsCount()).isZero();
        assertThat(result.getContractAgreementsCount()).isZero();
        assertThat(result.getContractDefinitionsCount()).isZero();
        assertThat(result.getPoliciesCount()).isZero();
        assertThat(result.getTransferProcessDto().getIncomingTransferProcessCounts()).isEmpty();
        assertThat(result.getTransferProcessDto().getOutgoingTransferProcessCounts()).isEmpty();
        assertThat(result.getAssetsCount()).isZero();
    }
}
