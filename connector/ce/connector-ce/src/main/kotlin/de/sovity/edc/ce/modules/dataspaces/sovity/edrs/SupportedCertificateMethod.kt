/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.runtime.modules.model.DocumentedEnum

enum class SupportedCertificateMethod(override val documentation: String) : DocumentedEnum {
    RSA4096("RSA 4096 bits encryption"),
    RSA2048("RSA 2048 bits encryption, the minimum allowed.");
}
