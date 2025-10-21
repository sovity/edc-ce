/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset

import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.pages.asset_detail_page.services.AssetDetailPageBuilder
import de.sovity.edc.ce.api.ui.pages.asset_detail_page.services.AssetDetailPageQueryService
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService
import de.sovity.edc.ce.api.utils.jooq.EdcJsonUtils
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
    private val assetDetailPageBuilder: AssetDetailPageBuilder,
    private val assetDetailPageQueryService: AssetDetailPageQueryService,
    private val jsonUtils: EdcJsonUtils
) {

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
        val foundAsset =
            assetDetailPageQueryService.fetchAssetDetailPage(dsl, assetId)
                ?.let { assetDetailPageBuilder.buildAsset(it) }
                ?: notFoundError("Asset with ID $assetId not found")
        val editedAsset = assetMapper.editAsset(foundAsset, request)
        dsl.fetchOne(a, a.ASSET_ID.eq(assetId))?.also {
            it.dataAddress = jsonUtils.toPostgresqlJson(editedAsset.dataAddress.properties)
            it.properties = jsonUtils.toPostgresqlJson(editedAsset.properties)
            it.privateProperties = jsonUtils.toPostgresqlJson(editedAsset.privateProperties)
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
