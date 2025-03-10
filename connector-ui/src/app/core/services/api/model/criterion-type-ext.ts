/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {UiCriterionOperator} from '@sovity.de/edc-client';

export const CRITERION_OPERATOR_SYMBOLS: Record<UiCriterionOperator, string> = {
  EQ: '=',
  IN: 'in',
  LIKE: 'like',
};
