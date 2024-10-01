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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc;

import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.SovityEdcRuntime;

public class Main {
    public static void main(String[] args) {
        SovityEdcRuntime.boot(ConfigProps.ALL_CE_PROPS);
    }
}
