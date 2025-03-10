/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services;

import de.sovity.edc.ce.api.common.model.BuildInfo;
import de.sovity.edc.ce.config.CeConfigProps;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

@RequiredArgsConstructor
@Service
public class VersionsService {

    private final Config config;

    public BuildInfo getVersions() {
        return new BuildInfo(
            getBuildDate(),
            getBuildVersion()
        );
    }

    private String getBuildVersion() {
        return CeConfigProps.getSOVITY_BUILD_VERSION().getStringOrThrow(config);
    }

    private String getBuildDate() {
        return CeConfigProps.getSOVITY_BUILD_DATE().getStringOrThrow(config);
    }
}
