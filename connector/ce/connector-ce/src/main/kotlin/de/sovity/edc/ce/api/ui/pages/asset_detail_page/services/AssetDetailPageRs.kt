/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset_detail_page.services

import org.jooq.JSON

data class AssetDetailPageRs(
    val assetId: String,
    val createdAt: Long,
    val properties: JSON,
    val privateProperties: JSON,
    val dataAddress: JSON
)
