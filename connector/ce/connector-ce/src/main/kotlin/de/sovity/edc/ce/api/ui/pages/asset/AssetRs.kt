/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset

import de.sovity.edc.ce.api.common.model.AssetListPageFilter
import de.sovity.edc.ce.api.common.model.AssetListSortProperty
import de.sovity.edc.ce.api.utils.filterTable
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.jsonField
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.parseMap
import de.sovity.edc.ce.api.utils.queryTable
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.spi.types.domain.DataAddress
import org.jooq.DSLContext
import org.jooq.JSON

class AssetRs(
    val assetId: String,
    val createdAt: Long,
    val properties: JSON,
    val privateProperties: JSON,
    val dataAddress: JSON
) {
    companion object {
        private val a = Tables.EDC_ASSET

        private val idField = jsonField(a.PROPERTIES, Asset.PROPERTY_ID)
        private val titleField = jsonField(a.PROPERTIES, Prop.Dcterms.TITLE)
        private val descriptionField =
            jsonField(a.PROPERTIES, Prop.Dcterms.DESCRIPTION)
        private val dataSourceAvailabilityField = jsonField(a.PROPERTIES, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY)

        private val searchableFields = listOf(
            idField,
            titleField,
            descriptionField,
            dataSourceAvailabilityField
        )

        fun listAssets(dsl: DSLContext, filter: AssetListPageFilter): List<AssetRs> {
            return selectAssetRs(dsl).filterTable(
                filter = filter,
                searchableFields = searchableFields,
                extractSortField = { name ->
                    when (name!!) {
                        AssetListSortProperty.TITLE -> titleField
                        AssetListSortProperty.DESCRIPTION_SHORT_TEXT -> descriptionField
                    }
                },
            ).fetchInto(
                AssetRs::class.java
            )
        }

        fun fetchAsset(dsl: DSLContext, assetId: String): AssetRs? =
            selectAssetRs(dsl).where(a.ASSET_ID.eq(assetId)).fetchOneInto(AssetRs::class.java)

        fun countAssets(dsl: DSLContext, filter: AssetListPageFilter): Int =
            dsl.selectCount().from(a).queryTable(filter, searchableFields).fetchSingleInto(Int::class.java)

        private fun selectAssetRs(dsl: DSLContext) = dsl.select(
            AssetRs::assetId from { a.ASSET_ID },
            AssetRs::createdAt from { a.CREATED_AT },
            AssetRs::properties from { a.PROPERTIES },
            AssetRs::privateProperties from { a.PRIVATE_PROPERTIES },
            AssetRs::dataAddress from { a.DATA_ADDRESS }
        ).from(a)
    }

    fun toAsset(): Asset {

        return Asset.Builder.newInstance()
            .id(assetId)
            .createdAt(createdAt)
            .properties(properties.parseMap())
            .privateProperties(privateProperties.parseMap())
            .dataAddress(
                DataAddress.Builder.newInstance()
                    .properties(
                        dataAddress.parseMap()
                    ).build()
            )
            .build()
    }
}
