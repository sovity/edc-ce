/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {EdcUiFeature} from '../../config/profiles/edc-ui-feature';

export interface NavItem {
  testId: string;
  path: string;
  title: string;
  icon: string;
  requiresFeature?: EdcUiFeature;
}
