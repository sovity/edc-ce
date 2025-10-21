/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.dataspaces.sphinx

import de.sovity.edc.client.EdcClient
import de.sovity.edc.client.gen.model.OperatorDto
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.PolicyTestUtils
import de.sovity.edc.extension.e2e.junit.utils.Consumer
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.Provider
import de.sovity.edc.runtime.config.ConfigUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import java.time.OffsetDateTime
import java.util.UUID

abstract class SphinxPoliciesTestBase {
    private lateinit var policyTestUtils: PolicyTestUtils
    private lateinit var consumerConfig: ConfigUtils
    private lateinit var providerConfig: ConfigUtils

    private val yesterday = OffsetDateTime.now().minusDays(1).toString()
    private val tomorrow = OffsetDateTime.now().plusDays(1).toString()
    private lateinit var testId: String

    @BeforeEach
    fun setup(
        clientAndServer: ClientAndServer,
        @Consumer @ControlPlane consumerClient: EdcClient,
        @Consumer @ControlPlane consumerConfig: ConfigUtils,
        @Provider @ControlPlane providerClient: EdcClient,
        @Provider @ControlPlane providerConfig: ConfigUtils
    ) {
        this.testId = UUID.randomUUID().toString()
        this.providerConfig = providerConfig
        this.consumerConfig = consumerConfig
        this.policyTestUtils = PolicyTestUtils(
            consumerClient = consumerClient,
            providerClient = providerClient,
            consumerConfig = consumerConfig,
            providerConfig = providerConfig,
            clientAndServer = clientAndServer
        )
    }


    @Test
    fun `given POLICY_EVALUATION_TIME, when gt yesterday, expect allowed`() {
        // arrange
        val assetId = "time-restricted-gt-yesterday-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.GT,
            yesterday
        )

        // act
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when geq yesterday, expect allowed`() {
        // arrange
        val assetId = "time-restricted-geq-yesterday-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.GEQ,
            yesterday
        )

        // act
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }


    @Test
    fun `given POLICY_EVALUATION_TIME, when lt yesterday, expect unallowed`() {
        // arrange
        val assetId = "time-restricted-lt-yesterday-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.LT,
            yesterday
        )

        // act
        policyTestUtils.checkUnavailable(assetId)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when leq yesterday, expect unallowed`() {
        // arrange
        val assetId = "time-restricted-leq-yesterday-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.LEQ,
            yesterday
        )

        // act
        policyTestUtils.checkUnavailable(assetId)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when lt tomorrow, expect allowed`() {
        // arrange
        val assetId = "time-restricted-lt-tomorrow-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.LT,
            tomorrow
        )

        // act
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when leq tomorrow, expect allowed`() {
        // arrange
        val assetId = "time-restricted-leq-tomorrow-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.LEQ,
            tomorrow
        )

        // act
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when gt tomorrow, expect unallowed`() {
        // arrange
        val assetId = "time-restricted-gt-tomorrow-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.GT,
            tomorrow
        )

        // act
        policyTestUtils.checkUnavailable(assetId)
    }

    @Test
    fun `given POLICY_EVALUATION_TIME, when geq tomorrow, expect unallowed`() {
        // arrange
        val assetId = "time-restricted-geq-tomorrow-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "POLICY_EVALUATION_TIME",
            OperatorDto.GEQ,
            tomorrow
        )

        // act
        policyTestUtils.checkUnavailable(assetId)
    }


    @Test
    fun `given sphinxDid, when eq-self, expect disallowed`() {
        // arrange
        val assetId = "referring-connector-eq-self-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.EQ,
            providerConfig.participantId
        )

        // act & assert
        policyTestUtils.checkUnavailable(assetId)
    }

    @Test
    fun `given sphinxDid, when eq-counterparty, expect allowed`() {
        // arrange
        val assetId = "referring-connector-eq-counterparty-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.EQ,
            consumerConfig.participantId
        )

        // act & assert
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }


    @Test
    fun `given sphinxDid, when eq-doesnotexist, expect disallowed`() {
        // arrange
        val assetId = "referring-connector-eq-doesnotexist-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.EQ,
            "doesnotexist"
        )

        // act & assert
        policyTestUtils.checkUnavailable(assetId)
    }


    @Test
    fun `given sphinxDid, when eq-empty, expect disallowed`() {
        // arrange
        val assetId = "referring-connector-eq-empty-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.EQ,
            ""
        )

        // act & assert
        policyTestUtils.checkUnavailable(assetId)
    }


    @Test
    fun `given sphinxDid, when eq-doesnotexist-comma-counterparty with space, expect allowed`() {
        // arrange
        val assetId = "referring-connector-eq-doesnotexist-comma-counterparty-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.EQ,
            "doesnotexist, ${consumerConfig.participantId}"
        )

        // act & assert
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }


    @Test
    fun `given sphinxDid, when in-doesnotexist-comma-counterparty, expect allowed`() {
        // arrange
        val assetId = "referring-connector-in-doesnotexist-comma-counterparty-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.IN,
            "doesnotexist, ${consumerConfig.participantId}"
        )

        // act & assert
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }

    @Test
    fun `given sphinxDid, when in-empty, expect disallowed`() {
        // arrange
        val assetId = "referring-connector-in-empty-$testId"
        policyTestUtils.createDataOffer(
            assetId,
            "sphinxDid",
            OperatorDto.IN,
            ""
        )

        // act & assert
        policyTestUtils.checkUnavailable(assetId)
    }

    @Test
    fun `given sphinxDid, when in-list-doesnotexist-and-counterparty, expect allowed`() {
        // arrange
        val assetId = "referring-connector-in-list-doesnotexist-and-counterparty-$testId"
        policyTestUtils.createDataOfferList(
            assetId,
            "sphinxDid",
            OperatorDto.IN,
            listOf("doesnotexist", consumerConfig.participantId)
        )

        // act & assert
        val dataOffer = policyTestUtils.checkCatalogWorks(assetId)
        policyTestUtils.checkPushTransferWorks(dataOffer)
    }

    @Test
    fun `given sphinxDid, when in-list-doesnotexist, expect disallowed`() {
        // arrange
        val assetId = "referring-connector-in-list-doesnotexist-$testId"
        policyTestUtils.createDataOfferList(
            assetId,
            "sphinxDid",
            OperatorDto.IN,
            listOf("doesnotexist")
        )

        // act & assert
        policyTestUtils.checkUnavailable(assetId)
    }
}


