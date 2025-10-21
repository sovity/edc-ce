/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.jsonField
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.db.jooq.tables.EdcAsset
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset

class AssetsPageFields(
    val asset: EdcAsset = Tables.EDC_ASSET,
) {
    val title = jsonField(asset.PROPERTIES, Prop.Dcterms.TITLE)
    val description = jsonField(asset.PROPERTIES, Prop.Dcterms.DESCRIPTION)
    val id = jsonField(asset.PROPERTIES, Asset.PROPERTY_ID)
    val dataSourceAvailability = jsonField(asset.PROPERTIES, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY)
}
