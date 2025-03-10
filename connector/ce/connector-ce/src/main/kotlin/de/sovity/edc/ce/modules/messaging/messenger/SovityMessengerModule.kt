/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger

import de.sovity.edc.runtime.modules.model.EdcModule

object SovityMessengerModule {
    fun instance() = EdcModule(
        name = "messaging",
        documentation = "Easy custom EDC-to-EDC messaging"
    ).apply {
        serviceExtensions(SovityMessengerExtension::class.java)
    }
}
