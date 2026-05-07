/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.railway

import de.sovity.edc.ce.modules.dataspaces.catena.CatenaDataspaceFeatures.tractusControlPlane
import de.sovity.edc.ce.modules.dataspaces.catena.CatenaDataspaceFeatures.tractusDataPlane
import de.sovity.edc.runtime.modules.model.EdcModule

object RailwayDataspaceFeatures {

    fun railwayControlPlane() = EdcModule(
        name = "railway-control-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM, while disabling the DataExchangeGovernanceCredential"
    ).apply {
        tractusControlPlane(disableGovernanceCredential = true)
    }

    fun railwayDataPlane() = EdcModule(
        name = "railway-data-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM, while disabling the DataExchangeGovernanceCredential"
    ).apply {
        tractusDataPlane(disableGovernanceCredential = true)
    }
}
