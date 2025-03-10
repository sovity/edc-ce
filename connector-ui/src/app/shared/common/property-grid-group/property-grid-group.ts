/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {PropertyGridField} from '../property-grid/property-grid-field';

export interface PropertyGridGroup {
  groupLabel: string | null;
  properties: PropertyGridField[];
}
