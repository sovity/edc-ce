/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.policies.evaluation_time

import de.sovity.edc.ce.modules.policy_utils.creator.RightExpressionParsers
import de.sovity.edc.ce.modules.policy_utils.creator.SimplePolicyCreator
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.result.ServiceResult
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import java.time.OffsetDateTime

/**
 * Validates the time of negotiation / transfer initiation
 */
class EvaluationTimePolicyExtension : ServiceExtension {
    @Inject
    private lateinit var simplePolicyCreator: SimplePolicyCreator

    override fun name(): String = javaClass.name

    override fun initialize(context: ServiceExtensionContext) {
        simplePolicyCreator.registerPolicyFunction(
            leftExpressionName = "POLICY_EVALUATION_TIME",
            leftExpressionValueFn = { ServiceResult.success(listOf(OffsetDateTime.now().toInstant())) },
            rightExpressionParser = RightExpressionParsers::offsetDateTimesAsInstants
        )
    }
}
