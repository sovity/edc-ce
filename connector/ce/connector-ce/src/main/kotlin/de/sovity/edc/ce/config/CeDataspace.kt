/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.config

import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedEnum
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

@Suppress("MaxLineLength")
enum class CeDataspace(override val documentation: String) : DocumentedEnum {
    SOVITY_MOCK_IAM("Configures the connector with sovity dataspace features, but in demo mode, mocking all dataspace central component interaction and without Connector-to-Connector IAM"),
    SOVITY_DAPS("Configures the connector for use in sovity dataspaces that use the sovity DAPS"),
    SOVITY_DAPS_OMEJDN("Configures the connector for use in legacy self-hosted sovity dataspaces that use the now deprecated Omejdn DAPS"),
    SPHIN_X("Configures the connector for use in Sphin-X."),
    CATENA_X("Configures the connector Catena-X compliant and Tractus-X compatible while further enhancing it with sovity features")
}

fun EdcModule.withCeDataspaceChoice() = apply {
    property(
        ConfigPropCategory.IMPORTANT,
        CeConfigProps.SOVITY_DATASPACE_KIND
    ) {
        required()
        enumValues(CeDataspace::class.java)
    }
}

/**
 * Add a module to the current module if the given dataspace variant is selected
 */
fun EdcModule.moduleIfCeDataspace(dataspace: CeDataspace, module: EdcModule) = apply {
    moduleIf(
        condition = DocumentedFn(
            "`${CeConfigProps.SOVITY_DATASPACE_KIND.property}`=`${dataspace.nameKebabCase}`"
        ) { config ->
            dataspace.isSelectedOption(CeConfigProps.SOVITY_DATASPACE_KIND.getStringOrEmpty(config))
        },
        module = module,
        documentation = dataspace.documentation
    )
}
