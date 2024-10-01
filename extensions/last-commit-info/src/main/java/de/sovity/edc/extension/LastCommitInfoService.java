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
import org.eclipse.edc.spi.system.configuration.Config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

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
        return this.readFileInCurrentClassClasspath("jar-last-commit-info.txt").trim();
    }

    private String getEnvLastCommitInfo() {
        return ConfigProps.EDC_LAST_COMMIT_INFO.getStringOrThrow(config);
    }

    private String getJarBuildDate() {
        return readFileInCurrentClassClasspath("jar-build-date.txt").trim();
    }

    private String getEnvBuildDate() {
        return ConfigProps.EDC_BUILD_DATE.getStringOrThrow(config);
    }

    private String readFileInCurrentClassClasspath(String path) {
        var classLoader = LastCommitInfoService.class.getClassLoader();
        var is = classLoader.getResourceAsStream(path);
        var scanner = new Scanner(Objects.requireNonNull(is), StandardCharsets.UTF_8).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}





