/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {removeNullValues} from '@/lib/utils/map-utils';

export const buildMailtoUrl = (opts: {
  email: string;
  subject?: string;
  body?: string;
  cc?: string;
  bcc?: string;
}): string => {
  const {email, ...queryParamValues} = opts;
  const queryParams = new URLSearchParams(removeNullValues(queryParamValues));
  // URLSearchParams replaces spaces with '+', so we need to replace them with '%20' for the mail scenario
  const queryParamsStr = queryParams.toString().replaceAll('+', '%20');
  const queryStr = queryParamsStr ? `?${queryParamsStr}` : '';
  return `mailto:${email}${queryStr}`;
};
