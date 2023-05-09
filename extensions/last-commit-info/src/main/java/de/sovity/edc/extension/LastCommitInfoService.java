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

import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class LastCommitInfoService {

    private final ServiceExtensionContext context;

    private String readFileInCurrentClassClasspath(String path) {
        var classLoader = LastCommitInfoService.class.getClassLoader();
        var is = classLoader.getResourceAsStream(path);
        var scanner = new Scanner(Objects.requireNonNull(is), StandardCharsets.UTF_8).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public LastCommitInfoService(ServiceExtensionContext context) {
        this.context = context;
    }

    public String getJarLastCommitInfo() {
        return this.readFileInCurrentClassClasspath("jar-last-commit-info.txt");
    }

    public String getEnvLastCommitInfo() {
        return context.getSetting("edc.last.commit.info", "");
    }

    public String getLatestJarBuildDate() {
        return readFileInCurrentClassClasspath("jar-latest-build-date-info.txt");
    }

    public String getLatestEnvBuildDate() {
        return context.getSetting("edc.env.latest.build.date", "");
    }

    public LastCommitInfo getLastCommitInfo() {
        var lastCommitInfo = new LastCommitInfo();
        lastCommitInfo.setEnvLastCommitInfo(getEnvLastCommitInfo());
        lastCommitInfo.setJarLastCommitInfo(getJarLastCommitInfo());

        lastCommitInfo.setJarLatestBuildDate(getLatestJarBuildDate());
        lastCommitInfo.setEnvLatestBuildDate(getLatestEnvBuildDate());
        return lastCommitInfo;
    }
}
