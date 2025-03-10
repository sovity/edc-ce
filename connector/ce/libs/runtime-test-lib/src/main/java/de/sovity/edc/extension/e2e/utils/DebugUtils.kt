/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.utils

import java.lang.management.ManagementFactory

object DebugUtils {
    val isDebug by lazy {
        ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
    }
}
