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
import de.sovity.edc.utils.config.utils.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.boot.config.EnvironmentVariables;
import org.eclipse.edc.boot.config.SystemProperties;
import org.eclipse.edc.boot.system.ExtensionLoader;
import org.eclipse.edc.boot.system.ServiceLocator;
import org.eclipse.edc.boot.system.ServiceLocatorImpl;
import org.eclipse.edc.boot.system.runtime.BaseRuntime;

import java.util.List;

/**
 * Custom {@link BaseRuntime} for applying config defaults on EDC startup
 */
@RequiredArgsConstructor
public class SovityEdcRuntime extends BaseRuntime {

    public SovityEdcRuntime(List<ConfigProp> configProps) {
        this(new ServiceLocatorImpl(), configProps);
    }

    public SovityEdcRuntime(ServiceLocator serviceLocator, List<ConfigProp> configProps) {
        // overwrite private final "extensionLoader"
        var extensionLoader = new ExtensionLoader(serviceLocator);
        ReflectionUtils.setFieldValue(BaseRuntime.class, this, "extensionLoader", extensionLoader);

        // overwrite private final "configLoader"
        var configurationLoader = new SovityConfigurationLoader(serviceLocator, EnvironmentVariables.ofDefault(),
            SystemProperties.ofDefault(), configProps);
        ReflectionUtils.setFieldValue(BaseRuntime.class, this, "configurationLoader", configurationLoader);
    }

    public static void boot(String[] args, List<ConfigProp> configProps) {
        // overwrite private static final "programArgs"
        ReflectionUtils.setFieldValue(BaseRuntime.class, null, "programArgs", args);

        var runtime = new SovityEdcRuntime(configProps);
        runtime.boot(true);
    }
}
