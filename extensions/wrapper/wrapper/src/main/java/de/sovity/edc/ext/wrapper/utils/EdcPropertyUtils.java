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

package de.sovity.edc.ext.wrapper.utils;

import lombok.NoArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.Map;

import static de.sovity.edc.ext.wrapper.utils.MapUtils.mapValues;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EdcPropertyUtils {

    public static Map<String, Object> mapPrivateProperties(Map<String, String> properties) {
        return mapValues(properties, s -> s);
    }

    public static DataAddress addressForProperties(Map<String, String> properties) {
        var addressProperties = mapValues(properties, s -> (Object) s);
        return DataAddress.Builder.newInstance().properties(addressProperties).build();
    }
}
