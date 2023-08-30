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

package de.sovity.edc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.UiCriterionDto;
import de.sovity.edc.client.gen.model.UiCriterionLiteralDto;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractOfferMapper;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.CriterionMapper;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
public class ContractNegotiationApiServiceTest {

    ObjectMapper objectMapper;
    ContractOfferMapper contractOfferMapper;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        objectMapper = new ObjectMapper();
        contractOfferMapper = new ContractOfferMapper(objectMapper);
    }

    @Test
    void testContractNegotiationInitialization(ContractNegotiationService negotiationService) {

        // arrange
        var client = TestUtils.edcClient();

        var contractNegotiationRequest = ContractNegotiationRequest.builder()
                .protocol("http")
                .counterPartyAddress("http://localhost:8080")
                .contractOffer(contractOfferMapper.buildContractOffer())
                .build();

        // act
        var response = client.uiApi().initiateContractNegotiation(contractNegotiationRequest);

        // assert
        assertThat(response).isNotNull();

    }
}
