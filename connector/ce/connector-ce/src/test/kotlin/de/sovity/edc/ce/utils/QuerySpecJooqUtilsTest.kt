/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.utils

import de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.TractusContractRetirementToSovityContractTerminationQuerySpec.FIELDS
import de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.TractusContractRetirementToSovityContractTerminationQuerySpec.FIELD_AGREEMENT_ID
import de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.TractusContractRetirementToSovityContractTerminationQuerySpec.FIELD_AGREEMENT_RETIREMENT_DATE
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.limitAndOffset
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.orderBy
import de.sovity.edc.ce.utils.QuerySpecJooqUtils.where
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.edc.spi.query.Criterion
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.CONTAINS
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.EQUAL
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.ILIKE
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.IN
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.LESS_THAN
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.LIKE
import org.eclipse.edc.spi.query.CriterionOperatorRegistry.NOT_EQUAL
import org.eclipse.edc.spi.query.QuerySpec
import org.eclipse.edc.spi.query.SortOrder
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.junit.jupiter.api.Test
import de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION as CONTRACT_TERMINATION

class QuerySpecJooqUtilsTest {
    companion object {
        val dsl = DSL.using(DefaultConfiguration())
    }

    @Test
    fun `can convert an empty query spec`() {
        val spec = QuerySpec.Builder.newInstance().build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)
        assertThat(select.toString()).isEqualTo(
            """
                select
                  "public"."sovity_contract_termination"."contract_agreement_id",
                  "public"."sovity_contract_termination"."reason",
                  "public"."sovity_contract_termination"."detail",
                  "public"."sovity_contract_termination"."terminated_at",
                  "public"."sovity_contract_termination"."terminated_by"
                from "public"."sovity_contract_termination"
                limit 50
                offset 0
            """.trimIndent()
        )
    }

    @Test
    fun `can select by agreement id`() {
        val spec = QuerySpec.Builder.newInstance()
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, "=", "fieldId"))
            .build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)
        assertThat(select.toString()).isEqualTo(
            """
                select
                  "public"."sovity_contract_termination"."contract_agreement_id",
                  "public"."sovity_contract_termination"."reason",
                  "public"."sovity_contract_termination"."detail",
                  "public"."sovity_contract_termination"."terminated_at",
                  "public"."sovity_contract_termination"."terminated_by"
                from "public"."sovity_contract_termination"
                where "public"."sovity_contract_termination"."contract_agreement_id" = 'fieldId'
                limit 50
                offset 0
            """.trimIndent()
        )
    }

    @Test
    fun `can select by multiple operators`() {
        val spec = QuerySpec.Builder.newInstance()
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, EQUAL, "fieldIdEqOperand"))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, NOT_EQUAL, "fieldIdNeqOperand"))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, IN, listOf("a", "b", "c")))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, LIKE, "fieldId%Like%Operand"))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, ILIKE, "fieldId%Ilike%Operand"))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, CONTAINS, "fieldIdContainsOperand"))
            .filter(Criterion.criterion(FIELD_AGREEMENT_ID, LESS_THAN, "fieldIdLessThanOperand"))
            .build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)

        assertThat(select.toString()).isEqualTo(
            """
              select
                "public"."sovity_contract_termination"."contract_agreement_id",
                "public"."sovity_contract_termination"."reason",
                "public"."sovity_contract_termination"."detail",
                "public"."sovity_contract_termination"."terminated_at",
                "public"."sovity_contract_termination"."terminated_by"
              from "public"."sovity_contract_termination"
              where (
                "public"."sovity_contract_termination"."contract_agreement_id" = 'fieldIdEqOperand'
                and "public"."sovity_contract_termination"."contract_agreement_id" <> 'fieldIdNeqOperand'
                and "public"."sovity_contract_termination"."contract_agreement_id" in ('[a, b, c]')
                and "public"."sovity_contract_termination"."contract_agreement_id" like 'fieldId%Like%Operand'
                and "public"."sovity_contract_termination"."contract_agreement_id" ilike 'fieldId%Ilike%Operand'
                and "public"."sovity_contract_termination"."contract_agreement_id" like (('%' || replace(
                  replace(
                    replace('fieldIdContainsOperand', '!', '!!'),
                    '%',
                    '!%'
                  ),
                  '_',
                  '!_'
                )) || '%') escape '!'
                and "public"."sovity_contract_termination"."contract_agreement_id" < 'fieldIdLessThanOperand'
              )
              limit 50
              offset 0
            """.trimIndent()
        )
    }

    @Test
    fun `limit and offset`() {
        val spec = QuerySpec.Builder.newInstance()
            .limit(123)
            .offset(456)
            .build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)
        assertThat(select.toString()).isEqualTo(
            """
                select
                  "public"."sovity_contract_termination"."contract_agreement_id",
                  "public"."sovity_contract_termination"."reason",
                  "public"."sovity_contract_termination"."detail",
                  "public"."sovity_contract_termination"."terminated_at",
                  "public"."sovity_contract_termination"."terminated_by"
                from "public"."sovity_contract_termination"
                limit 123
                offset 456
            """.trimIndent()
        )
    }

    @Test
    fun `integer field`() {
        val spec = QuerySpec.Builder.newInstance()
            .filter(
                Criterion.criterion(
                    FIELD_AGREEMENT_RETIREMENT_DATE,
                    "=",
                    123456
                )
            )
            .build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)
        assertThat(select.toString()).isEqualTo(
            """
                select
                  "public"."sovity_contract_termination"."contract_agreement_id",
                  "public"."sovity_contract_termination"."reason",
                  "public"."sovity_contract_termination"."detail",
                  "public"."sovity_contract_termination"."terminated_at",
                  "public"."sovity_contract_termination"."terminated_by"
                from "public"."sovity_contract_termination"
                where "public"."sovity_contract_termination"."terminated_at" = timestamp with time zone '1970-01-01 00:02:03.456+00:00'
                limit 50
                offset 0
            """.trimIndent()
        )
    }

    @Test
    fun `can order by field`() {
        val spec = QuerySpec.Builder.newInstance()
            .sortOrder(SortOrder.ASC)
            .sortField(FIELD_AGREEMENT_RETIREMENT_DATE)
            .build()

        val select = dsl.selectFrom(CONTRACT_TERMINATION)
            .where(spec, FIELDS)
            .orderBy(spec, FIELDS)
            .limitAndOffset(spec)
        assertThat(select.toString()).isEqualTo(
            """
                select
                  "public"."sovity_contract_termination"."contract_agreement_id",
                  "public"."sovity_contract_termination"."reason",
                  "public"."sovity_contract_termination"."detail",
                  "public"."sovity_contract_termination"."terminated_at",
                  "public"."sovity_contract_termination"."terminated_by"
                from "public"."sovity_contract_termination"
                order by "public"."sovity_contract_termination"."terminated_at" asc
                limit 50
                offset 0
            """.trimIndent()
        )
    }
}

