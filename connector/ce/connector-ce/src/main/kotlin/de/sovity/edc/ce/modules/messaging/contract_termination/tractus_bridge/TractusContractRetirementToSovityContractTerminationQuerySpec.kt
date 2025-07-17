/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge

import de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.buildCondition
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.buildSortField
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.direction
import org.eclipse.edc.spi.query.Criterion
import org.eclipse.edc.spi.query.SortOrder
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object TractusContractRetirementToSovityContractTerminationQuerySpec {
    const val FIELD_AGREEMENT_ID: String = "agreementId"
    const val FIELD_REASON: String = "reason"
    const val FIELD_AGREEMENT_RETIREMENT_DATE: String = "agreementRetirementDate"

    val FIELDS = listOf(
        QuerySpecToJooqMapping(
            FIELD_AGREEMENT_ID,
            toCondition = { expression: Criterion ->
                buildCondition(
                    SOVITY_CONTRACT_TERMINATION.CONTRACT_AGREEMENT_ID,
                    expression.operator,
                    expression.operandRight.toString()
                )
            },
            toSortField = { order: SortOrder ->
                SOVITY_CONTRACT_TERMINATION.CONTRACT_AGREEMENT_ID.direction(order)
            }
        ),
        QuerySpecToJooqMapping(
            FIELD_REASON,
            toCondition = { expression: Criterion ->
                buildCondition(
                    SOVITY_CONTRACT_TERMINATION.DETAIL,
                    expression.operator,
                    expression.operandRight.toString()
                )
            },
            toSortField = { order: SortOrder ->
                buildSortField(SOVITY_CONTRACT_TERMINATION.DETAIL, order)
            }
        ),
        QuerySpecToJooqMapping(
            FIELD_AGREEMENT_RETIREMENT_DATE,
            toCondition = { expression: Criterion ->
                buildCondition(
                    SOVITY_CONTRACT_TERMINATION.TERMINATED_AT,
                    expression.operator,
                    getRightOperandAsOffsetDateTime(expression)
                )
            },
            toSortField = { order: SortOrder ->
                buildSortField(SOVITY_CONTRACT_TERMINATION.TERMINATED_AT, order)
            }
        ),
    )

    private fun getRightOperandAsOffsetDateTime(expression: Criterion): OffsetDateTime =
        OffsetDateTime.ofInstant(
            Instant.ofEpochMilli(
                when (val operandRight = expression.operandRight) {
                    is Long -> operandRight
                    is Number -> operandRight.toLong()
                    is String -> try {
                        operandRight.toString().toLong()
                    } catch (e: NumberFormatException) {
                        error("Don't know how to convert $operandRight to a Long: ${e.message}")
                    }

                    else -> error("Don't know how to convert $operandRight to a Long")
                }
            ),
            ZoneOffset.UTC
        )

}
