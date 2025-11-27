/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper

import de.sovity.edc.client.EdcClient
import de.sovity.edc.client.gen.model.ContractNegotiationRequest
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState
import de.sovity.edc.client.gen.model.ContractsPageRequest
import de.sovity.edc.client.gen.model.DataOfferCreateRequest
import de.sovity.edc.client.gen.model.DataOfferPolicyCreateType
import de.sovity.edc.client.gen.model.DataOfferPublishType
import de.sovity.edc.client.gen.model.DataSourceType
import de.sovity.edc.client.gen.model.OperatorDto
import de.sovity.edc.client.gen.model.UiAssetCreateRequest
import de.sovity.edc.client.gen.model.UiContractNegotiation
import de.sovity.edc.client.gen.model.UiContractOffer
import de.sovity.edc.client.gen.model.UiDataOffer
import de.sovity.edc.client.gen.model.UiDataSinkHttpDataPush
import de.sovity.edc.client.gen.model.UiDataSource
import de.sovity.edc.client.gen.model.UiDataSourceHttpData
import de.sovity.edc.client.gen.model.UiInitiateTransferRequest
import de.sovity.edc.client.gen.model.UiInitiateTransferType
import de.sovity.edc.client.gen.model.UiPolicyConstraint
import de.sovity.edc.client.gen.model.UiPolicyExpression
import de.sovity.edc.client.gen.model.UiPolicyExpressionType
import de.sovity.edc.client.gen.model.UiPolicyLiteral
import de.sovity.edc.client.gen.model.UiPolicyLiteralType
import de.sovity.edc.extension.e2e.utils.getUrl
import de.sovity.edc.runtime.config.ConfigUtils
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.time.Duration

class PolicyTestUtils(
    val consumerClient: EdcClient,
    val providerClient: EdcClient,
    val consumerConfig: ConfigUtils,
    val providerConfig: ConfigUtils,
    val clientAndServer: ClientAndServer
) {

    fun createDataOffer(
        dataOfferId: String,
        leftExpression: String,
        operator: OperatorDto,
        rightExpression: String
    ) {
        createDataOfferInternal(
            dataOfferId,
            leftExpression,
            operator,
            UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value(rightExpression)
                .build()
        )
    }

    fun createDataOfferList(
        dataOfferId: String,
        leftExpression: String,
        operator: OperatorDto,
        rightExpression: List<String?>?
    ) {
        createDataOfferInternal(
            dataOfferId,
            leftExpression,
            operator,
            UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING_LIST)
                .valueList(rightExpression)
                .build()
        )
    }

    private fun createDataOfferInternal(
        dataOfferId: String,
        leftExpression: String,
        operator: OperatorDto,
        rightExpression: UiPolicyLiteral
    ) {
        val relativeUrl = "/data-source/$dataOfferId"
        val url = clientAndServer.getUrl(relativeUrl)
        clientAndServer.`when`(HttpRequest.request(relativeUrl).withMethod("GET")).respond {
            HttpResponse.response()
                .withStatusCode(200)
                .withHeader("Content-Type", "text/plain")
                .withBody("data for $dataOfferId")
        }

        val dataSource: UiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(
                UiDataSourceHttpData.builder()
                    .baseUrl(url)
                    .build()
            )
            .build()

        val asset: UiAssetCreateRequest = UiAssetCreateRequest.builder()
            .id(dataOfferId)
            .dataSource(dataSource)
            .build()

        val expression: UiPolicyExpression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(
                UiPolicyConstraint.builder()
                    .left(leftExpression)
                    .operator(operator)
                    .right(rightExpression)
                    .build()
            )
            .build()

        val request = DataOfferCreateRequest.builder()
            .asset(asset)
            .publishType(DataOfferPublishType.PUBLISH_RESTRICTED)
            .policyCreateType(DataOfferPolicyCreateType.EXPRESSION)
            .policyExpression(expression)
            .build()

        providerClient.uiApi().createDataOffer(request)
    }

    fun checkUnavailable(assetId: String) {
        val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(
            providerConfig.participantId,
            providerConfig.protocolApiUrl
        )

        // assert
        assertThat(dataOffers).extracting<String> { it.asset.assetId }.doesNotContain(assetId)
    }

    fun checkCatalogWorks(assetId: String): UiDataOffer {
        val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(
            providerConfig.participantId,
            providerConfig.protocolApiUrl
        )

        // assert
        assertThat(dataOffers).extracting<String> { it.asset.assetId }.contains(assetId)
        val dataOffer = dataOffers.single { it.asset.assetId == assetId }
        assertThat(dataOffer.contractOffers.single().policy.errors).isEmpty()
        return dataOffer
    }

    fun checkPushTransferWorks(dataOffer: UiDataOffer) {
        // Negotiate
        val dataOfferId = dataOffer.asset.assetId
        val contractOffer = dataOffer.contractOffers.single()
        val contractAgreementId = negotiateAgreement(dataOffer, contractOffer)

        // Check consumer policy errors
        checkPoliciesErrorFree(dataOffer, contractOffer, contractAgreementId)

        // Check transfer
        val relativeUrl = "/data-sink/$dataOfferId"
        val url = clientAndServer.getUrl(relativeUrl)

        var actualData = ""
        clientAndServer.`when`(HttpRequest.request(relativeUrl).withMethod("POST")).respond {
            actualData = it.bodyAsString
            HttpResponse.response()
                .withStatusCode(200)
        }


        val transferRequest = UiInitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .type(UiInitiateTransferType.HTTP_DATA_PUSH)
            .httpDataPush(
                UiDataSinkHttpDataPush.builder()
                    .baseUrl(url)
                    .build()
            )
            .build()

        consumerClient.uiApi().initiateTransferV2(transferRequest)

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted {
            assertThat(actualData).isEqualTo("data for $dataOfferId")
        }
    }

    private fun checkPoliciesErrorFree(
        dataOffer: UiDataOffer,
        contractOffer: UiContractOffer,
        contractAgreementId: String
    ) {
        // Check catalog policy errors
        assertThat(contractOffer.policy.errors).isEmpty()

        // Check consumer policy errors
        val consumerAgreement = consumerClient.uiApi().contractDetailPage(contractAgreementId)
        assertThat(consumerAgreement.contractPolicy.errors).isEmpty()

        // Check provider policy errors
        val providerAgreementId = providerClient.uiApi().contractsPage(
            ContractsPageRequest.builder()
                .searchText(dataOffer.asset.assetId)
                .build()
        ).contracts.single { it.assetId == dataOffer.asset.assetId }.contractAgreementId
        val providerAgreement = providerClient.uiApi().contractDetailPage(providerAgreementId)
        assertThat(providerAgreement.contractPolicy.errors).isEmpty()
    }

    private fun negotiateAgreement(
        dataOffer: UiDataOffer,
        contractOffer: UiContractOffer
    ): String {
        val negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyId(dataOffer.participantId)
            .counterPartyAddress(dataOffer.endpoint)
            .assetId(dataOffer.asset.assetId)
            .contractOfferId(contractOffer.contractOfferId)
            .policyJsonLd(contractOffer.policy.policyJsonLd)
            .build()

        val negotiationId = consumerClient.uiApi().initiateContractNegotiation(negotiationRequest)
            .contractNegotiationId

        val negotiation = Awaitility.await().atMost(Duration.ofSeconds(15)).until(
            { consumerClient.uiApi().getContractNegotiation(negotiationId) },
            { contractNegotiation: UiContractNegotiation ->
                contractNegotiation.state.simplifiedState != ContractNegotiationSimplifiedState.IN_PROGRESS
            }
        )
        assertThat(negotiation.state.simplifiedState).isEqualTo(ContractNegotiationSimplifiedState.AGREED)
        val contractAgreementId = negotiation.contractAgreementId!!
        return contractAgreementId
    }
}


