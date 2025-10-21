/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.jsonField
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.db.jooq.tables.EdcAsset
import de.sovity.edc.ce.db.jooq.tables.EdcContractAgreement
import de.sovity.edc.ce.db.jooq.tables.EdcContractNegotiation
import de.sovity.edc.ce.db.jooq.tables.SovityContractTermination
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.jooq.impl.DSL

class ContractsPageFields(
    val agreement: EdcContractAgreement = Tables.EDC_CONTRACT_AGREEMENT,
    val termination: SovityContractTermination = Tables.SOVITY_CONTRACT_TERMINATION,
    val negotiation: EdcContractNegotiation = Tables.EDC_CONTRACT_NEGOTIATION,
    val asset: EdcAsset = Tables.EDC_ASSET,
) {
    val creator = jsonField(asset.PROPERTIES, Prop.Dcterms.CREATOR)
    val transferProcessCount = DSL.field(
        DSL.selectCount().from(Tables.EDC_TRANSFER_PROCESS)
            .where(Tables.EDC_TRANSFER_PROCESS.CONTRACT_ID.eq(agreement.AGR_ID))
    )
    val assetTitle = DSL.coalesce(jsonField(asset.PROPERTIES, Prop.Dcterms.TITLE), agreement.ASSET_ID)
}
