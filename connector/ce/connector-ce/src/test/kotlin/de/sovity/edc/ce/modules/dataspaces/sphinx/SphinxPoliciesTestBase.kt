/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.dataspaces.sphinx

import de.sovity.edc.client.EdcClient
import de.sovity.edc.client.gen.model.DataOfferCreateRequest
import de.sovity.edc.client.gen.model.DataOfferPublishType
import de.sovity.edc.client.gen.model.DataSourceType
import de.sovity.edc.client.gen.model.OperatorDto
import de.sovity.edc.client.gen.model.UiAssetCreateRequest
import de.sovity.edc.client.gen.model.UiDataSource
import de.sovity.edc.client.gen.model.UiDataSourceHttpData
import de.sovity.edc.client.gen.model.UiPolicyConstraint
import de.sovity.edc.client.gen.model.UiPolicyExpression
import de.sovity.edc.client.gen.model.UiPolicyExpressionType
import de.sovity.edc.client.gen.model.UiPolicyLiteral
import de.sovity.edc.client.gen.model.UiPolicyLiteralType
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote
import de.sovity.edc.extension.e2e.junit.utils.Consumer
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.Provider
import de.sovity.edc.runtime.config.ConfigUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

abstract class SphinxPoliciesTestBase {
    private lateinit var dataAddress: TestBackendRemote
    private lateinit var consumerClient: EdcClient
    private lateinit var providerClient: EdcClient
    private lateinit var consumerConfig: ConfigUtils
    private lateinit var providerConfig: ConfigUtils

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
    fun testPolicies() {
        val yesterday = OffsetDateTime.now().minusDays(1).toString()
        val tomorrow = OffsetDateTime.now().plusDays(1).toString()

        // Time Restriction
        createDataOffer(
            "time-restricted-gt-yesterday",
            "inForceDate",
            OperatorDto.GT,
            yesterday
        )
        createDataOffer(
            "time-restricted-geq-yesterday",
            "inForceDate",
            OperatorDto.GEQ,
            yesterday
        )
        createDataOffer(
            "time-restricted-lt-tomorrow",
            "inForceDate",
            OperatorDto.LT,
            tomorrow
        )
        createDataOffer(
            "time-restricted-leq-tomorrow",
            "inForceDate",
            OperatorDto.LEQ,
            tomorrow
        )

        // Referring Connector
        // EQ
        createDataOffer(
            "referring-connector-eq-self",
            "sphinxDid",
            OperatorDto.EQ,
            providerConfig.participantId
        )
        createDataOffer(
            "referring-connector-eq-counterparty",
            "sphinxDid",
            OperatorDto.EQ,
            consumerConfig.participantId
        )
        createDataOffer(
            "referring-connector-eq-random",
            "sphinxDid",
            OperatorDto.EQ,
            "random"
        )
        createDataOffer(
            "referring-connector-eq-empty",
            "sphinxDid",
            OperatorDto.EQ,
            ""
        )
        createDataOffer(
            "referring-connector-eq-random-comma-counterparty",
            "sphinxDid",
            OperatorDto.EQ,
            "random, ${consumerConfig.participantId}"
        )

        // IN
        createDataOffer(
            "referring-connector-in-random-comma-counterparty",
            "sphinxDid",
            OperatorDto.IN,
            "random, ${consumerConfig.participantId}"
        )
        createDataOffer(
            "referring-connector-in-empty",
            "sphinxDid",
            OperatorDto.IN,
            ""
        )
        createDataOfferList(
            "referring-connector-in-list-random-and-counterparty",
            "sphinxDid",
            OperatorDto.IN,
            listOf("random", consumerConfig.participantId)
        )
        createDataOfferList(
            "referring-connector-in-list-random",
            "sphinxDid",
            OperatorDto.IN,
            listOf("random")
        )

        // act
        val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(
            providerConfig.participantId,
            providerConfig.protocolApiUrl
        )

        // assert
        val ids = dataOffers.map { it.asset.assetId }
        assertThat(ids).containsExactlyInAnyOrder(
            "time-restricted-gt-yesterday",
            "time-restricted-geq-yesterday",
            "time-restricted-lt-tomorrow",
            "time-restricted-leq-tomorrow",
            "referring-connector-eq-counterparty",
            "referring-connector-in-random-comma-counterparty",
            "referring-connector-in-list-random-and-counterparty"
        )
    }

    private fun createDataOffer(
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

    private fun createDataOfferList(
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
        val dataSource: UiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(
                UiDataSourceHttpData.builder()
                    .baseUrl("http://0.0.0.0")
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
            .policyExpression(expression)
            .build()

        providerClient.uiApi().createDataOffer(request)
    }
}


