/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.boot.system.runtime.BaseRuntime;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Custom {@link BaseRuntime} for applying config defaults on EDC startup
 */
@RequiredArgsConstructor
public class SovityEdcRuntime extends BaseRuntime {
    /**
     * Will be evaluated in sequence to apply default config values and validate the configuration.
     */
    private final List<ConfigProp> configProps;

    @Override
    protected @NotNull ServiceExtensionContext createContext(Monitor monitor) {
        return new SovityServiceExtensionContext(monitor, configProps, this.loadConfigurationExtensions());
    }

    public static void boot(List<ConfigProp> configProps) {
        var runtime = new SovityEdcRuntime(configProps);

        // This method is protected, so we need to have the static method here
        runtime.boot();
    }
}
