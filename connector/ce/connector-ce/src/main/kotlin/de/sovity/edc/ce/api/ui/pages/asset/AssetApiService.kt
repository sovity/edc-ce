/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset

import de.sovity.edc.ce.api.common.model.AssetListPageFilter
import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest
import de.sovity.edc.ce.api.ui.model.AssetListPage
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService
import de.sovity.edc.ce.api.utils.buildTablePage
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.toPostgresqlJson
import de.sovity.edc.ce.api.utils.notFoundError
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.libs.mappers.AssetMapper
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdBuilder
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext
import org.jooq.JSON

@Service
class AssetApiService(
    private val assetMapper: AssetMapper,
    private val assetIdValidator: AssetIdValidator,
    private val selfDescriptionService: SelfDescriptionService,
    private val assetJsonLdBuilder: AssetJsonLdBuilder,
) {

    fun assetListPage(dsl: DSLContext, filter: AssetListPageFilter): AssetListPage {
        val connectorEndpoint = selfDescriptionService.connectorEndpoint
        val participantId = selfDescriptionService.participantId
        val assets = AssetRs.listAssets(dsl, filter).map {
            assetMapper.buildUiAsset(it.toAsset(), connectorEndpoint, participantId)
        }
        return buildTablePage(
            clazz = AssetListPage::class.java,
            content = assets,
            totalItems = AssetRs.countAssets(dsl, filter),
            currentPage = filter.page,
            optionalPageSize = filter.pageSize
        )
    }

    fun assetDetailsPage(dsl: DSLContext, assetId: String): UiAsset {
        val connectorEndpoint = selfDescriptionService.connectorEndpoint
        val participantId = selfDescriptionService.participantId
        return AssetRs.fetchAsset(dsl, assetId)?.let {
            assetMapper.buildUiAsset(it.toAsset(), connectorEndpoint, participantId)
        } ?: notFoundError("Asset with ID $assetId not found")
    }

    fun createAsset(dsl: DSLContext, request: UiAssetCreateRequest): IdResponseDto {
        assetIdValidator.assertValid(request.id)
        val organizationName = selfDescriptionService.curatorName
        val createAssetJson = assetJsonLdBuilder.buildCreateAssetJsonLds(request, organizationName)
        val at = Tables.EDC_ASSET
        dsl.newRecord(at).also {
            it.assetId = request.id
            it.createdAt = System.currentTimeMillis()
            it.dataAddress = JSON.json(createAssetJson.dataSource.toString())
            it.properties = JSON.json(createAssetJson.properties.toString())
            it.privateProperties = JSON.json(createAssetJson.privateProperties.toString())
            it.insert()
        }
        return IdResponseDto(request.id)
    }

    fun editAsset(dsl: DSLContext, assetId: String, request: UiAssetEditRequest): IdResponseDto {
        val a = Tables.EDC_ASSET
        val foundAsset = AssetRs.fetchAsset(dsl, assetId) ?: notFoundError("Asset with ID $assetId not found")
        val editedAsset = assetMapper.editAsset(foundAsset.toAsset(), request)
        dsl.fetchOne(a, a.ASSET_ID.eq(assetId))?.also {
            it.dataAddress = editedAsset.dataAddress.properties.toPostgresqlJson()
            it.properties = editedAsset.properties.toPostgresqlJson()
            it.privateProperties = editedAsset.privateProperties.toPostgresqlJson()
            it.update()
        }
        return IdResponseDto(assetId)
    }

    fun deleteAsset(dsl: DSLContext, assetId: String): IdResponseDto {
        val a = Tables.EDC_ASSET
        dsl.deleteFrom(a).where(a.ASSET_ID.eq(assetId)).execute()
        return IdResponseDto(assetId)
    }
}
