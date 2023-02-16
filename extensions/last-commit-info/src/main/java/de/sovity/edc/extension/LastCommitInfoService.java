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

    public LastCommitInfoService(ServiceExtensionContext context) {
        this.context = context;
    }

    public String getLastCommitInfo() {
        var result = "";

        if (!getEnvLastCommitInfo().equals("")){
            result += "Env Last Commit Info: \n";
            result += getEnvLastCommitInfo() + "\n";
        }

        if(!getEnvLastCommitInfo().equals(getJarLastCommitInfo())){
            result += "Jar Last Commit Info: \n";
            result += getJarLastCommitInfo();
        }

        return result;
    }

    public String getJarLastCommitInfo() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var is = classLoader.getResourceAsStream("jar-last-commit-info.txt");
        var scanner = new Scanner(Objects.requireNonNull(is), StandardCharsets.UTF_8).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public String getEnvLastCommitInfo() {
        return context.getSetting("edc.last.commit.info", "");
    }
}
