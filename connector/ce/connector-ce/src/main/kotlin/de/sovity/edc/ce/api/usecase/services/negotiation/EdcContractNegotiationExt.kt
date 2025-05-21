/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.negotiation

import de.sovity.edc.ce.db.jooq.tables.EdcContractNegotiation
import org.jooq.impl.DSL

fun EdcContractNegotiation.getAssetId() = DSL.jsonGetAttributeAsText(
    DSL.jsonGetElement(CONTRACT_OFFERS, 0), "assetId"
)
