/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset;

import de.sovity.edc.runtime.simple_di.Service;
import org.apache.commons.lang3.Validate;

import java.util.regex.Pattern;

@Service
public class AssetIdValidator {
    private final Pattern pattern = Pattern.compile("^[^\\s:]+$");

    public boolean isValid(String assetId) {
        return pattern.matcher(assetId).matches();
    }

    public void assertValid(String assetId) {
        Validate.isTrue(isValid(assetId), "Asset ID must not contain colons or whitespaces.");
    }
}
