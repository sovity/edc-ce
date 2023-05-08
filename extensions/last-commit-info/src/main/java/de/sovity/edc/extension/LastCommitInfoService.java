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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class LastCommitInfoService {

    private final ServiceExtensionContext context;


    public LastCommitInfoService(ServiceExtensionContext context) {
        this.context = context;
    }

    public LastCommitInfo getLastCommitInfo() throws IOException {
        var result = new LastCommitInfo();
        result.setEnvLastCommitInfo(getEnvLastCommitInfo());
        result.setEnvLastBuildDate(getEnvLastCommitDate());

        result.setJarLastCommitInfo(getJarInfo().get(0));
        result.setJarLastBuildDate(getJarInfo().get(1));
        return result;
    }

    public List<String> getJarInfo() throws IOException {

        var jarInfo = new ArrayList<String>();
        var classLoader = Thread.currentThread().getContextClassLoader();
        var is = classLoader.getResourceAsStream("jar-last-commit-info.txt");
        var scanner = new Scanner(Objects.requireNonNull(is), StandardCharsets.UTF_8).useDelimiter("\\A");
        jarInfo.add(scanner.hasNext() ? scanner.next() : "");

        Manifest manifest = new Manifest(is);
        Attributes attributes = manifest.getMainAttributes();
        jarInfo.add(attributes.getValue("Build-Date"));

        return jarInfo;
    }

    public String getEnvLastCommitInfo() {
        return context.getSetting("edc.last.commit.info", "");
    }

    public String getEnvLastCommitDate() {
        return context.getSetting("edc.env.last.commit.date", "");
    }


}
