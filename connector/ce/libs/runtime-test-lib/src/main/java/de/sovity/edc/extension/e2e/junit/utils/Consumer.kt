/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

/**
 * In test code, in conjunction with [CeE2eTestExtension], specifies that the injected instance must come from the Consumer EDC
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Consumer
