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

package de.sovity.edc.extension;

import de.sovity.edc.utils.config.ConfigProps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.eclipse.edc.spi.system.configuration.Config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
public class LastCommitInfoService {

    private final Config config;

    public LastCommitInfo getLastCommitInfo() {
        var lastCommitInfo = new LastCommitInfo();
        lastCommitInfo.setEnvLastCommitInfo(getEnvLastCommitInfo());
        lastCommitInfo.setJarLastCommitInfo(getJarLastCommitInfo());

        lastCommitInfo.setJarBuildDate(getJarBuildDate());
        lastCommitInfo.setEnvBuildDate(getEnvBuildDate());
        return lastCommitInfo;
    }

    private String getJarLastCommitInfo() {
        return this.readClasspathFile("jar-last-commit-info.txt").trim();
    }

    private String getEnvLastCommitInfo() {
        return ConfigProps.EDC_LAST_COMMIT_INFO.getStringOrThrow(config);
    }

    private String getJarBuildDate() {
        return readClasspathFile("jar-build-date.txt").trim();
    }

    private String getEnvBuildDate() {
        return ConfigProps.EDC_BUILD_DATE.getStringOrThrow(config);
    }

    @SneakyThrows
    private String readClasspathFile(String fileName) {
        var is = getClass().getClassLoader().getResourceAsStream(fileName);
        Objects.requireNonNull(is, "File not found: " + fileName);
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}





