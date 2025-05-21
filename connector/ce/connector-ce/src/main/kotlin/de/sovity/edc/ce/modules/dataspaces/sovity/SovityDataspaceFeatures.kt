/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity

import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrApiServiceV3GenericExtension
import de.sovity.edc.ce.modules.dataspaces.sovity.iam.DapsModules
import de.sovity.edc.ce.modules.dataspaces.sovity.iam.IamMockModule
import de.sovity.edc.ce.modules.dataspaces.sovity.policies.SovityPoliciesModule
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractTerminationModule
import de.sovity.edc.runtime.modules.model.EdcModule

object SovityDataspaceFeatures {
    fun sovityControlPlaneMockIam() = EdcModule(
        name = "sovity-dataspace-cp-with-mock-iam",
        documentation = "Dataspace-specific control-plane features for sovity dataspaces with iam-mock"
    ).apply {
        module(IamMockModule.instance())
        controlPlaneFeatures()
    }

    fun sovityControlPlaneDaps() = EdcModule(
        name = "sovity-dataspace-cp-with-daps",
        documentation = "Dataspace-specific control-plane features for sovity dataspaces with DAPS"
    ).apply {
        module(DapsModules.sovityDaps())
        controlPlaneFeatures()
    }

    fun sovityControlPlaneDapsOmejdn() = EdcModule(
        name = "sovity-dataspace-cp-with-daps-omejdn",
        documentation = "Dataspace-specific control-plane features for sovity dataspaces with DAPS Omejdn"
    ).apply {
        module(DapsModules.sovityDapsOmejdn())
        controlPlaneFeatures()
    }

    private fun EdcModule.controlPlaneFeatures() {
        modules(
            SovityPoliciesModule.instance(),
            ContractTerminationModule.instance()
        )
        dependencyBundles(CeDependencyBundles.sovityControlPlane)
        serviceExtensions(EdrApiServiceV3GenericExtension::class.java)
    }
}
