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
import org.eclipse.edc.boot.config.ConfigurationLoader;
import org.eclipse.edc.boot.config.EnvironmentVariables;
import org.eclipse.edc.boot.config.SystemProperties;
import org.eclipse.edc.boot.system.ServiceLocator;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;

import java.util.List;

/**
 * Custom {@link ServiceExtensionContext} for applying config defaults on EDC startup
 */
public class SovityConfigurationLoader extends ConfigurationLoader {
    private final List<ConfigProp> configProps;

    public SovityConfigurationLoader(
        ServiceLocator serviceLocator,
        EnvironmentVariables environmentVariables,
        SystemProperties systemProperties,
        List<ConfigProp> configProps
    ) {
        super(serviceLocator, environmentVariables, systemProperties);
        this.configProps = configProps;
    }

    @Override
    public Config loadConfiguration(Monitor monitor) {
        var config = super.loadConfiguration(monitor);
        return applyDefaults(monitor, config, configProps);
    }

    public static Config applyDefaults(Monitor monitor, Config rawConfig, List<ConfigProp> configProps) {
        var configService = new ConfigService(monitor, configProps);
        return ConfigFactory.fromMap(configService.applyDefaults(rawConfig.getEntries()));
    }
}
