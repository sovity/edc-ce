/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsImpl
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject
import org.eclipse.edc.connector.dataplane.selector.spi.instance.DataPlaneInstance
import org.eclipse.edc.spi.system.configuration.Config

data class DataPlaneInstanceDto(
    val id: String,
    val allowedTransferTypes: Set<String>,
    val allowedSourceTypes: Set<String>,
    val allowedDestTypes: Set<String>,
    val dpPublicApiUrl: String,
    val dpDataflowsEndpoint: String
) {
    companion object {
        /**
         * Assumes this code path is a data plane.
         * Describes itself to either an external or an internal control plane.
         */
        fun describeSelf(config: Config, configUtils: ConfigUtils): DataPlaneInstanceDto {
            val dataflowsEndpoint = ConfigUtilsImpl.Endpoints.dataPlaneDataflowsEndpoint(
                dpControlApiUrl = configUtils.controlApiUrl
            )

            val allowedTransferTypes = CeConfigProps.EDC_DATAPLANE_TRANSFERTYPES.getListNonEmpty(config).toSet()
            val allowedSourceTypes = CeConfigProps.EDC_DATAPLANE_SOURCETYPES.getListOrEmpty(config).toSet()
            val allowedDestTypes = CeConfigProps.EDC_DATAPLANE_DESTTYPES.getListOrEmpty(config).toSet()

            return DataPlaneInstanceDto(
                id = configUtils.publicApiUrl,
                allowedTransferTypes = allowedTransferTypes,
                allowedSourceTypes = allowedSourceTypes,
                allowedDestTypes = allowedDestTypes,
                dpPublicApiUrl = configUtils.publicApiUrl,
                dpDataflowsEndpoint = dataflowsEndpoint,
            )
        }

        /**
         * Converts a [DataPlaneInstance] to a [DataPlaneInstanceDto].
         *
         * Only for comparison purposes.
         */
        fun fromInstance(instance: DataPlaneInstance): DataPlaneInstanceDto {
            return DataPlaneInstanceDto(
                id = instance.id,
                dpDataflowsEndpoint = instance.url.toString(),
                allowedTransferTypes = instance.allowedTransferTypes,
                allowedSourceTypes = instance.allowedSourceTypes,
                allowedDestTypes = instance.allowedDestTypes,
                dpPublicApiUrl = instance.properties.getOrDefault(Prop.Edc.PUBLIC_API_URL, "") as String
            )
        }
    }

    fun toInstance(): DataPlaneInstance {
        return DataPlaneInstance.Builder.newInstance()
            .id(id)
            .url(dpDataflowsEndpoint)
            .allowedTransferType(allowedTransferTypes)
            .allowedSourceTypes(allowedSourceTypes)
            .allowedDestTypes(allowedDestTypes)
            .property(Prop.Edc.PUBLIC_API_URL, dpPublicApiUrl)
            .build()
    }

    fun toCreateRequestJsonLd(): JsonObject {
        return Json.createObjectBuilder()
            .add(Prop.ID, id)
            .add(Prop.Edc.URL, dpDataflowsEndpoint)
            .add(Prop.Edc.ALLOWED_SOURCE_TYPES, Json.createArrayBuilder(allowedSourceTypes))
            .add(Prop.Edc.ALLOWED_DEST_TYPES, Json.createArrayBuilder(allowedDestTypes))
            .add(Prop.Edc.ALLOWED_TRANSFER_TYPES, Json.createArrayBuilder(allowedTransferTypes))
            .add(Prop.Edc.PROPERTIES, Json.createObjectBuilder().add(Prop.Edc.PUBLIC_API_URL, dpPublicApiUrl))
            .build()
    }
}
