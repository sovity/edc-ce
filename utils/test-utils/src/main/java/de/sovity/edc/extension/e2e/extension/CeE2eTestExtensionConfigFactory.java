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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.e2e.extension;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CeE2eTestExtensionConfigFactory {

    public static E2eTestExtensionConfig defaultBuilder() {
        return E2eTestExtensionConfig.builder().moduleName(":launchers:connectors:sovity-dev").build();
    }

    public static E2eTestExtensionConfig withModule(String module) {
        return defaultBuilder().toBuilder().moduleName(module).build();
    }
}
