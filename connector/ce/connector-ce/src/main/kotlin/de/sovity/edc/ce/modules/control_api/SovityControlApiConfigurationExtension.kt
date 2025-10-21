/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.control_api

import com.fasterxml.jackson.databind.DeserializationFeature
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.simple_di.MigrationSensitive
import org.eclipse.edc.connector.api.control.configuration.ControlApiConfigurationExtension
import org.eclipse.edc.jsonld.spi.JsonLd
import org.eclipse.edc.jsonld.spi.JsonLdKeywords
import org.eclipse.edc.jsonld.spi.Namespaces
import org.eclipse.edc.policy.model.OdrlNamespace
import org.eclipse.edc.runtime.metamodel.annotation.Configuration
import org.eclipse.edc.runtime.metamodel.annotation.Extension
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.runtime.metamodel.annotation.Setting
import org.eclipse.edc.runtime.metamodel.annotation.Settings
import org.eclipse.edc.spi.EdcException
import org.eclipse.edc.spi.constants.CoreConstants
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.system.apiversion.ApiVersionService
import org.eclipse.edc.spi.system.apiversion.VersionRecord
import org.eclipse.edc.spi.types.TypeManager
import org.eclipse.edc.web.jersey.providers.jsonld.JerseyJsonLdInterceptor
import org.eclipse.edc.web.jersey.providers.jsonld.ObjectMapperProvider
import org.eclipse.edc.web.spi.WebService
import org.eclipse.edc.web.spi.configuration.ApiContext
import org.eclipse.edc.web.spi.configuration.PortMapping
import org.eclipse.edc.web.spi.configuration.PortMappingRegistry
import org.eclipse.edc.web.spi.configuration.context.ControlApiUrl
import java.io.IOException
import java.net.URI
import java.util.stream.Stream

/**
 * Tells all the Control API controllers under which context alias they need to register their resources: either
 * `default` or `control`
 *
 * Overrides the EDC Core ControlApiConfigurationExtension to use sovity.edc.fqdn.internal instead of edc.hostname
 */
@MigrationSensitive(
    changes = "Uses sovity.edc.fqdn.internal in function controlApiUrl instead of edc.hostname",
    dependsOn = ControlApiConfigurationExtension::class
)
@Extension(value = SovityControlApiConfigurationExtension.NAME)
@Provides(ControlApiUrl::class)
class SovityControlApiConfigurationExtension : ServiceExtension {
    companion object {
        const val NAME: String = "Control API configuration"
        private const val CONTROL_SCOPE: String = "CONTROL_API"
        private const val DEFAULT_CONTROL_PORT: Int = 9191
        private const val DEFAULT_CONTROL_PATH: String = "/api/control"
        private const val API_VERSION_JSON_FILE = "control-api-version.json"
    }

    @Setting(
        description = "Configures endpoint for reaching the Control API. If it's missing it defaults to the hostname configuration.",
        key = "edc.control.endpoint",
        required = false
    )
    private var controlEndpoint: String? = null

    @Configuration
    private lateinit var apiConfiguration: ControlApiConfiguration

    @Inject
    private lateinit var portMappingRegistry: PortMappingRegistry

    @Inject
    private lateinit var webService: WebService

    @Inject
    private lateinit var jsonLd: JsonLd

    @Inject
    private lateinit var typeManager: TypeManager

    @Inject
    private lateinit var apiVersionService: ApiVersionService

    override fun name(): String = NAME

    override fun initialize(context: ServiceExtensionContext) {
        val portMapping = PortMapping(ApiContext.CONTROL, apiConfiguration.port, apiConfiguration.path)
        portMappingRegistry.register(portMapping)
        val jsonLdMapper = typeManager.getMapper(CoreConstants.JSON_LD)
        context.registerService(ControlApiUrl::class.java, controlApiUrl(context, portMapping))

        jsonLd.registerNamespace(CoreConstants.EDC_PREFIX, CoreConstants.EDC_NAMESPACE, CONTROL_SCOPE)
        jsonLd.registerNamespace(JsonLdKeywords.VOCAB, CoreConstants.EDC_NAMESPACE, CONTROL_SCOPE)
        jsonLd.registerNamespace(OdrlNamespace.ODRL_PREFIX, OdrlNamespace.ODRL_SCHEMA, CONTROL_SCOPE)
        jsonLd.registerNamespace(Namespaces.DSPACE_PREFIX, Namespaces.DSPACE_SCHEMA, CONTROL_SCOPE)

        webService.registerResource(ApiContext.CONTROL, ObjectMapperProvider(jsonLdMapper))
        webService.registerResource(
            ApiContext.CONTROL,
            JerseyJsonLdInterceptor(jsonLd, jsonLdMapper, CoreConstants.JSON_LD)
        )

        registerVersionInfo(javaClass.classLoader)
    }

    private fun registerVersionInfo(resourceClassLoader: ClassLoader) {
        try {
            resourceClassLoader.getResourceAsStream(API_VERSION_JSON_FILE).use { versionContent ->
                if (versionContent == null) {
                    throw EdcException("Version file not found or not readable.")
                }
                Stream.of(
                    *typeManager.mapper
                        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                        .readValue(
                            versionContent,
                            Array<VersionRecord>::class.java
                        )
                )
                    .forEach { vr: VersionRecord? -> apiVersionService.addRecord(ApiContext.CONTROL, vr) }
            }
        } catch (e: IOException) {
            throw EdcException(e)
        }
    }

    private fun controlApiUrl(context: ServiceExtensionContext, config: PortMapping): ControlApiUrl {
        // use sovity.edc.fqdn.internal as hostname
        val hostname = CeConfigProps.SOVITY_EDC_FQDN_INTERNAL.getStringOrThrow(context.config)
        val callbackAddress = controlEndpoint ?: "http://$hostname:${config.port}${config.path}"

        try {
            val url = URI.create(callbackAddress)
            return ControlApiUrl { url }
        } catch (e: IllegalArgumentException) {
            context.monitor.severe("Error creating control plane endpoint url", e)
            throw EdcException(e)
        }
    }

    @Settings
    @JvmRecord
    internal data class ControlApiConfiguration(
        @Setting(
            key = "web.http." + ApiContext.CONTROL + ".port",
            description = "Port for " + ApiContext.CONTROL + " api context",
            defaultValue = DEFAULT_CONTROL_PORT.toString() + ""
        )
        val port: Int,

        @Setting(
            key = "web.http." + ApiContext.CONTROL + ".path",
            description = "Path for " + ApiContext.CONTROL + " api context",
            defaultValue = DEFAULT_CONTROL_PATH
        )
        val path: String
    )
}
