/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

public class TextUtils {
    public String abbreviate(String text, int maxCharacters) {
        if (text == null) {
            return null;
        }
        return text.substring(0, Math.min(maxCharacters, text.length()));
    }
}
