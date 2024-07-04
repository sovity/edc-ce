/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.messenger.impl;

import org.eclipse.edc.spi.iam.ClaimToken;

import java.util.function.BiFunction;

public record HandlerWithClaims<IN, OUT>(Class<IN> clazz, BiFunction<ClaimToken, IN, OUT> handler) {
}
