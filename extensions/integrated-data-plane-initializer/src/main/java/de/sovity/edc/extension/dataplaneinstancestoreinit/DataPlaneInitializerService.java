/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.dataplaneinstancestoreinit;

import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.instance.DataPlaneInstance;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class DataPlaneInitializerService {
    private static final String INTEGRATED = "integrated";
    private final Monitor monitor;
    private final Config config;
    private final DataPlaneSelectorService dataPlaneSelectorService;

    @SneakyThrows
    public void addIntegratedDataPlane() {
        var dataPlaneUrl = ConfigUtils.getIntegratedDataPlaneUrl(config);
        var transferTypes = new HashSet<>(ConfigProps.EDC_DATAPLANE_TRANSFERTYPES.getListOrThrow(config));
        var destTypes = new HashSet<>(ConfigProps.EDC_DATAPLANE_DESTTYPES.getListOrThrow(config));
        var sourceTypes = new HashSet<>(ConfigProps.EDC_DATAPLANE_SOURCETYPES.getListOrThrow(config));
        var publicApiUrl = ConfigUtils.getPublicApiUrl(config);

        addDataPlane(
            INTEGRATED,
            dataPlaneUrl,
            transferTypes,
            sourceTypes,
            destTypes,
            publicApiUrl
        );
    }

    @SneakyThrows
    public void addDataPlane(
        String id,
        String dataPlaneUrl,
        Set<String> transferTypes,
        Set<String> sourceTypes,
        Set<String> destTypes,
        String publicUrl
    ) {
        if (hasCorrectlyConfiguredInstance(id, dataPlaneUrl, transferTypes)) {
            return;
        }

        var instance = DataPlaneInstance.Builder.newInstance()
            .id(id)
            .url(dataPlaneUrl)
            .allowedTransferType(transferTypes)
            .allowedSourceTypes(sourceTypes)
            .allowedDestTypes(destTypes)
            .property("publicApiUrl", publicUrl)
            .build();

        dataPlaneSelectorService.addInstance(instance)
            .orElseThrow(f -> new EdcException("Data Plane '%s' registration failed: " + f.getFailureDetail()));

        var message = "Data Plane Instance created: '%s' '%s' '%s'".formatted(
            id,
            String.join(", ", transferTypes),
            dataPlaneUrl
        );

        monitor.info(message);
    }

    private boolean hasCorrectlyConfiguredInstance(
        String id,
        String dataPlaneUrl,
        Set<String> transferTypes
    ) {
        var instance = dataPlaneSelectorService.findById(id).orElse(unused -> null);
        if (instance == null) {
            return false;
        }

        var isCorrectlyConfigured = transferTypes.equals(instance.getAllowedTransferTypes()) &&
            dataPlaneUrl.equals(instance.getUrl().toString());
        if (isCorrectlyConfigured) {
            monitor.info("Data Plane Instance '%s' is already correctly registered.".formatted(id));
            return true;
        }

        monitor.warning(
            "Data Plane instance '%s' is out of date. Since the DataPlaneInstance lacks an update method, the Data Plane Instance will be deleted for a moment. This can cause running transfers to fail."
        );
        dataPlaneSelectorService.unregister(id)
            .orElseThrow(f -> new EdcException("Data Plane '%s' deregistration failed: " + f.getFailureDetail()));
        return false;
    }
}
