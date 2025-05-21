/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.config_utils

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.config.AuthHeader
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.isProduction
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import org.eclipse.edc.spi.system.configuration.Config

class ConfigUtilsImpl(config: Config) : ConfigUtils {
    override val participantId = getParticipantId(config)
    override val protocolApiUrl = getProtocolApiUrl(config)
    override val managementApiUrl = getManagementApiUrl(config)
    override val managementApiKey = getManagementApiKey(config)
    override fun getManagementApiAuthHeader() = managementApiKey?.let { AuthHeader("x-api-key", it) }
    override val defaultApiUrl = getDefaultApiUrl(config)
    override val publicApiUrl = getPublicApiUrl(config)
    override val controlApiUrl = getControlApiUrl(config)
    override val proxyApiUrl = getProxyApiUrl(config)


    companion object {
        @JvmStatic
        fun getParticipantId(config: Config): String =
            CeConfigProps.EDC_PARTICIPANT_ID.getStringOrEmpty(config)

        @JvmStatic
        fun getProtocolApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                getPublicEdcHost(config, CeConfigProps.WEB_HTTP_PROTOCOL_PORT),
                CeConfigProps.WEB_HTTP_PROTOCOL_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getManagementApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                getPublicEdcHost(config, CeConfigProps.WEB_HTTP_MANAGEMENT_PORT),
                CeConfigProps.WEB_HTTP_MANAGEMENT_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getManagementApiKey(config: Config): String? =
            CeConfigProps.EDC_API_AUTH_KEY.getStringOrNull(config)

        @JvmStatic
        fun getDefaultApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                CeConfigProps.SOVITY_EDC_FQDN_INTERNAL.getStringOrEmpty(config) +
                    ":" + CeConfigProps.WEB_HTTP_DEFAULT_PORT.getStringOrEmpty(config),
                CeConfigProps.WEB_HTTP_DEFAULT_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getPublicApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                getPublicEdcHost(config, CeConfigProps.WEB_HTTP_PUBLIC_PORT),
                CeConfigProps.WEB_HTTP_PUBLIC_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getControlApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                CeConfigProps.SOVITY_EDC_FQDN_INTERNAL.getStringOrEmpty(config) +
                    ":" + CeConfigProps.WEB_HTTP_CONTROL_PORT.getStringOrEmpty(config),
                CeConfigProps.WEB_HTTP_CONTROL_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getProxyApiUrl(config: Config): String {
            return UrlPathUtils.urlPathJoin(
                CeConfigProps.SOVITY_HTTP_PROTOCOL.getStringOrEmpty(config),
                getPublicEdcHost(config, CeConfigProps.TX_EDC_DPF_CONSUMER_PROXY_PORT),
                CeConfigProps.WEB_HTTP_PROXY_PATH.getStringOrEmpty(config)
            )
        }

        @JvmStatic
        fun getPublicEdcHost(config: Config, portIfNoReverseProxy: ConfigPropRef): String {
            val hasReverseProxy = config.isProduction()

            var host = CeConfigProps.SOVITY_EDC_FQDN_PUBLIC.getStringOrEmpty(config)
            if (!hasReverseProxy) {
                host = "$host:${portIfNoReverseProxy.getStringOrEmpty(config)}"
            }
            return host
        }
    }

    object Endpoints {
        /**
         * Endpoint on the CP used by the DP to validate the tokens (?)
         *
         * Also used by the DP to tell the CP that a transfer is completed
         */
        @JvmStatic
        fun tokenValidationEndpoint(cpControlApiUrl: String): String {
            return UrlPathUtils.urlPathJoin(
                cpControlApiUrl,
                "/token"
            )
        }

        /**
         * Endpoint on the DP used by the CP to communicate with the DP
         */
        @JvmStatic
        fun dataPlaneDataflowsEndpoint(dpControlApiUrl: String): String {
            return UrlPathUtils.urlPathJoin(
                dpControlApiUrl,
                "/v1/dataflows"
            )
        }
    }
}
