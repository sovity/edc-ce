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

package de.sovity.edc.ext.brokerserver;

import java.util.List;


/**
 * Manual Dependency Injection result
 *
 * @param jaxRsResources Jax RS Resource implementations to register.
 */
public record BrokerServerExtensionContext(List<Object> jaxRsResources) {
}
