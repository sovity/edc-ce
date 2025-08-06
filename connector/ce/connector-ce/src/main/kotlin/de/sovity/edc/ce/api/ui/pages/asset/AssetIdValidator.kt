/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset

import de.sovity.edc.runtime.simple_di.Service
import org.apache.commons.lang3.Validate
import java.util.regex.Pattern

@Service
class AssetIdValidator {
    private val pattern: Pattern = Pattern.compile("^[^\\s:]+$")

    fun isValid(assetId: String) = pattern.matcher(assetId).matches()

    fun assertValid(assetId: String) {
        Validate.isTrue(isValid(assetId), "Asset ID must not contain colons or whitespaces.")
    }
}
