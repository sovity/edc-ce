/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.client.EdcClient
import de.sovity.edc.client.gen.model.ContractDefinitionRequest
import de.sovity.edc.client.gen.model.ContractNegotiationRequest
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState
import de.sovity.edc.client.gen.model.DataSourceType
import de.sovity.edc.client.gen.model.OperatorDto
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto
import de.sovity.edc.client.gen.model.UiAssetCreateRequest
import de.sovity.edc.client.gen.model.UiContractNegotiation
import de.sovity.edc.client.gen.model.UiContractOffer
import de.sovity.edc.client.gen.model.UiCriterion
import de.sovity.edc.client.gen.model.UiCriterionLiteral
import de.sovity.edc.client.gen.model.UiCriterionLiteralType
import de.sovity.edc.client.gen.model.UiCriterionOperator
import de.sovity.edc.client.gen.model.UiDataOffer
import de.sovity.edc.client.gen.model.UiDataSinkHttpDataPush
import de.sovity.edc.client.gen.model.UiDataSinkHttpDataPushMethod
import de.sovity.edc.client.gen.model.UiDataSource
import de.sovity.edc.client.gen.model.UiDataSourceHttpData
import de.sovity.edc.client.gen.model.UiInitiateTransferRequest
import de.sovity.edc.client.gen.model.UiInitiateTransferType
import de.sovity.edc.client.gen.model.UiPolicyConstraint
import de.sovity.edc.client.gen.model.UiPolicyExpression
import de.sovity.edc.client.gen.model.UiPolicyExpressionType
import de.sovity.edc.client.gen.model.UiPolicyLiteral
import de.sovity.edc.client.gen.model.UiPolicyLiteralType
import de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote
import de.sovity.edc.extension.e2e.junit.utils.Consumer
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.Provider
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

/**
 * This test is the CE because it is referenced from documentation
 */
abstract class ApiWrapperDemoTestBase {
    private lateinit var dataAddress: TestBackendRemote
    private lateinit var consumerClient: EdcClient
    private lateinit var providerClient: EdcClient
    private lateinit var consumerConfig: ConfigUtils
    private lateinit var providerConfig: ConfigUtils

    private val dataOfferData = "expected data 123"
    private val dataOfferId = "my-data-offer-2023-11"

    @BeforeEach
    fun setup(
        dataAddress: TestBackendRemote,
        @Consumer @ControlPlane consumerClient: EdcClient,
        @Consumer @ControlPlane consumerConfig: ConfigUtils,
        @Provider @ControlPlane providerClient: EdcClient,
        @Provider @ControlPlane providerConfig: ConfigUtils
    ) {
        this.dataAddress = dataAddress
        this.consumerClient = consumerClient
        this.consumerConfig = consumerConfig
        this.providerClient = providerClient
        this.providerConfig = providerConfig
    }

    @Test
    fun provide_and_consume() {
        // provider: create data offer
        createPolicy()
        createAsset()
        createContractDefinition()

        // consumer: negotiate contract and transfer data
        val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(
            providerConfig.participantId,
            providerConfig.protocolApiUrl
        )
        var negotiation = initiateNegotiation(dataOffers.single(), dataOffers.single().contractOffers.single())
        negotiation = awaitNegotiationDone(negotiation.contractNegotiationId)
        initiateTransfer(negotiation)

        // check data sink
        DataTransferTestUtil.validateDataTransferred(dataAddress.dataSinkSpyUrl, dataOfferData)
    }

    private fun createAsset() {
        val dataSource: UiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(
                UiDataSourceHttpData.builder()
                    .baseUrl(dataAddress.getDataSourceUrl(dataOfferData))
                    .build()
            )
            .build()

        val asset: UiAssetCreateRequest = UiAssetCreateRequest.builder()
            .id(dataOfferId)
            .title("My Data Offer")
            .description("Example Data Offer.")
            .version("2023-11")
            .language("EN")
            .publisherHomepage("https://my-department.my-org.com/my-data-offer")
            .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
            .dataSource(dataSource)
            .build()

        providerClient.uiApi().createAsset(asset)
    }

    private fun createPolicy() {
        val afterYesterday: UiPolicyExpression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(
                UiPolicyConstraint.builder()
                    .left("POLICY_EVALUATION_TIME")
                    .operator(OperatorDto.GT)
                    .right(
                        UiPolicyLiteral.builder()
                            .type(UiPolicyLiteralType.STRING)
                            .value(OffsetDateTime.now().minusDays(1).toString())
                            .build()
                    )
                    .build()
            )
            .build()

        val beforeTomorrow: UiPolicyExpression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(
                UiPolicyConstraint.builder()
                    .left("POLICY_EVALUATION_TIME")
                    .operator(OperatorDto.LT)
                    .right(
                        UiPolicyLiteral.builder()
                            .type(UiPolicyLiteralType.STRING)
                            .value(OffsetDateTime.now().plusDays(1).toString())
                            .build()
                    )
                    .build()
            )
            .build()

        val expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(listOf(afterYesterday, beforeTomorrow))
            .build()

        val policyDefinition = PolicyDefinitionCreateDto.builder()
            .policyDefinitionId(dataOfferId)
            .policyExpression(expression)
            .build()

        providerClient.uiApi().createPolicyDefinitionV2(policyDefinition)
    }

    private fun createContractDefinition() {
        val contractDefinition = ContractDefinitionRequest.builder()
            .contractDefinitionId(dataOfferId)
            .accessPolicyId(dataOfferId)
            .contractPolicyId(dataOfferId)
            .assetSelector(
                listOf<UiCriterion>(
                    UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(
                            UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build()
                        )
                        .build()
                )
            )
            .build()

        providerClient.uiApi().createContractDefinition(contractDefinition)
    }

    private fun initiateNegotiation(dataOffer: UiDataOffer, contractOffer: UiContractOffer): UiContractNegotiation {
        val negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyId(dataOffer.getParticipantId())
            .counterPartyAddress(dataOffer.getEndpoint())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(contractOffer.getContractOfferId())
            .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
            .build()

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest)
    }

    private fun awaitNegotiationDone(negotiationId: String): UiContractNegotiation {
        val negotiation = Awaitility.await().atMost(Duration.ofSeconds(15)).until(
            { consumerClient.uiApi().getContractNegotiation(negotiationId) },
            { contractNegotiation: UiContractNegotiation ->
                contractNegotiation.state.simplifiedState != ContractNegotiationSimplifiedState.IN_PROGRESS
            }
        )

        assertThat(negotiation.state.simplifiedState).isEqualTo(ContractNegotiationSimplifiedState.AGREED)
        return negotiation
    }

    private fun initiateTransfer(negotiation: UiContractNegotiation) {
        val contractAgreementId = negotiation.contractAgreementId!!

        val transferRequest = UiInitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .type(UiInitiateTransferType.HTTP_DATA_PUSH)
            .httpDataPush(
                UiDataSinkHttpDataPush.builder()
                    .baseUrl(dataAddress.dataSinkUrl)
                    .method(UiDataSinkHttpDataPushMethod.PUT)
                    .build()
            )
            .build()

        consumerClient.uiApi().initiateTransferV2(transferRequest)
    }
}


