/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset_detail_page.services

import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.ui.pages.asset.DataAddressBuilder
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService
import de.sovity.edc.ce.api.utils.jooq.EdcJsonUtils
import de.sovity.edc.ce.libs.mappers.AssetMapper
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset

@Service
class AssetDetailPageBuilder(
    private val dataAddressBuilder: DataAddressBuilder,
    private val selfDescriptionService: SelfDescriptionService,
    private val assetMapper: AssetMapper,
    private val jsonUtils: EdcJsonUtils,
) {
    fun buildAsset(detailPageRs: AssetDetailPageRs): Asset = Asset.Builder.newInstance()
        .id(detailPageRs.assetId)
        .createdAt(detailPageRs.createdAt)
        .properties(jsonUtils.parseMap(detailPageRs.properties))
        .privateProperties(jsonUtils.parseMap(detailPageRs.privateProperties))
        .dataAddress(dataAddressBuilder.buildDataAddress(detailPageRs.dataAddress))
        .build()

    fun buildUiAsset(detailPageRs: AssetDetailPageRs): UiAsset = assetMapper.buildUiAsset(
        buildAsset(detailPageRs),
        selfDescriptionService.connectorEndpoint,
        selfDescriptionService.participantId
    )
}
