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

package de.sovity.edc.extension.vaultinit;

import de.sovity.edc.utils.config.CeConfigProps;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.configuration.Config;

@RequiredArgsConstructor
public class VaultInitializerService {

    private final Monitor monitor;
    private final Config config;
    private final Vault vault;

    public void initializeVault() {
        if (!CeConfigProps.MY_EDC_VAULT_INIT_ENABLED.getBoolean(config)) {
            monitor.info("Vault initialization is disabled.");
            return;
        }
        monitor.info("Vault initialization is enabled.");

        var entries = CeConfigProps.MY_EDC_VAULT_INIT_ENTRIES_WILDCARD.getWildcardSubconfig(config);
        entries.getRelativeEntries().forEach(this::initializeVaultValue);
    }

    private void initializeVaultValue(String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value) || key.equals("*")) {
            return;
        }

        var currentValue = vault.resolveSecret(key);
        if (StringUtils.isNotBlank(currentValue)) {
            if (!currentValue.equals(value)) {
                monitor.info("Vault value is already present and different, skipping initialization: %s".formatted(key));
            }
            return;
        }

        vault.storeSecret(key, value);
        monitor.info("Initialized vault entry: %s".formatted(key));
    }
}
