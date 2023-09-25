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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import org.apache.commons.lang3.Validate;

import java.util.regex.Pattern;

public class AssetIdValidator {
    private final Pattern pattern = Pattern.compile("^[^\\s:]+$");

    public boolean isValid(String assetId) {
        return pattern.matcher(assetId).matches();
    }

    public void assertValid(String assetId) {
        Validate.isTrue(isValid(assetId), "Asset ID must not contain colons or whitespaces.");
    }
}
