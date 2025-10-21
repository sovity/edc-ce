/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

data class AssetsPageEntryRs(
    val assetId: String,
    val title: String?,
    val description: String?,
    val dataSourceAvailability: String?,
)
