/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.business_partner_group

import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupCreateSubmit
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupEditPage
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupEditSubmit
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupListPageEntry
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupQuery
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.fromArray
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.parseStringArray
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.toPostgresqlJsonArray
import de.sovity.edc.ce.api.utils.jooq.SearchUtils
import de.sovity.edc.ce.api.utils.notFoundError
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL

@Service
class BusinessPartnerGroupApiService {
    fun editPage(dsl: DSLContext, groupId: String): BusinessPartnerGroupEditPage {
        val bpg = Tables.EDC_BUSINESS_PARTNER_GROUP
        val members = dsl.select(bpg.BPN).from(bpg).where(bpg.GROUPS.contains(JSON.json(groupId)))
            .fetchInto(String::class.java)

        if (members.isEmpty()) {
            notFoundError("Business Partner Group with ID '$groupId' not found.")
        }

        return BusinessPartnerGroupEditPage(
            groupId,
            members
        )
    }

    fun editSubmit(dsl: DSLContext, groupId: String, request: BusinessPartnerGroupEditSubmit) =
        updateGroup(dsl, groupId, request.businessPartnerNumbers)

    fun createSubmit(dsl: DSLContext, request: BusinessPartnerGroupCreateSubmit) =
        updateGroup(dsl, request.groupId, request.businessPartnerNumbers)

    fun deleteSubmit(dsl: DSLContext, groupId: String) = updateGroup(dsl, groupId, emptyList())

    fun listPage(dsl: DSLContext, query: BusinessPartnerGroupQuery?): List<BusinessPartnerGroupListPageEntry> {
        val bpg = Tables.EDC_BUSINESS_PARTNER_GROUP

        val groupsFieldName = DSL.field(BusinessPartnerGroupRs::groupId.name, String::class.java)
        val bpnFieldName = DSL.field("bpn", String::class.java)

        val expandedTable = dsl.select(
            DSL.field(
                "json_array_elements_text({0})",
                String::class.java,
                bpg.GROUPS
            ).`as`(groupsFieldName),
            bpg.BPN.`as`(bpnFieldName)
        )
            .from(bpg)
            .asTable("expanded")


        val result = dsl.select(
            BusinessPartnerGroupRs::groupId from { groupsFieldName },
            BusinessPartnerGroupRs::businessPartnerNumbers fromArray { DSL.arrayAgg(bpnFieldName) }
        )
            .from(expandedTable)
            .where(
                SearchUtils.simpleSearch(query?.searchQuery, listOf(groupsFieldName))
            )
            .groupBy(groupsFieldName)
            .orderBy(groupsFieldName)
            .limit(query?.limit)
            .fetchInto(BusinessPartnerGroupRs::class.java)

        return result.map { BusinessPartnerGroupListPageEntry(it.groupId, it.businessPartnerNumbers) }
    }

    private data class BusinessPartnerGroupRs(
        val groupId: String,
        val businessPartnerNumbers: List<String>
    )

    private fun updateGroup(dsl: DSLContext, groupId: String, members: List<String>): IdResponseDto {
        val bpg = Tables.EDC_BUSINESS_PARTNER_GROUP
        val toUpdate =
            dsl.fetch(
                bpg,
                DSL.or(bpg.BPN.`in`(members), bpg.GROUPS.contains(JSON.json(groupId)))
            )

        toUpdate.forEach {
            var groups = it.groups.parseStringArray()

            if (members.contains(it.bpn)) {
                groups = (groups + groupId)
            } else if (groups.contains(groupId) && !members.contains(it.bpn)) {
                groups = (groups - groupId)
            }

            if (groups.isEmpty()) {
                it.delete()
            } else {
                it.groups = groups.distinct().toPostgresqlJsonArray()
                it.update()
            }
        }

        val toAdd = members.filter { bpn -> !toUpdate.any { bpn == it.bpn } }

        toAdd.forEach {
            dsl.newRecord(bpg).also { record ->
                record.bpn = it
                record.groups = listOf(groupId).toPostgresqlJsonArray()
                record.insert()
            }
        }

        return IdResponseDto(groupId)
    }
}
