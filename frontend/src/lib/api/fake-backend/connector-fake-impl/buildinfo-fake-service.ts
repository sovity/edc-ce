/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type BuildInfo} from '@sovity.de/edc-client';

export const buildInfo = (): BuildInfo => {
  return {
    buildDate: new Date().toISOString(),
    buildVersion: '0.0.0-local-dev',
  };
};
