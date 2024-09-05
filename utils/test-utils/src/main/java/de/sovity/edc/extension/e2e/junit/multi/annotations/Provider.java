/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.junit.multi.annotations;

import de.sovity.edc.extension.e2e.junit.multi.CeE2eTestExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * In test code, in conjunction with {@link CeE2eTestExtension}, specifies that the injected instance must come from the Provider EDC
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
}
