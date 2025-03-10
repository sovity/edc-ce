/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * We have multiple environment files that will replace environment.ts depending on active angular configuration.
 */
export interface EdcUiEnvironment {
  production: boolean;
}
