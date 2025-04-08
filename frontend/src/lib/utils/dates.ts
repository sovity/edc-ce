/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {format} from 'date-fns-tz';

export const getLocalDateTimeMinutesString = (date: Date): string =>
  format(date, 'dd/MM/yyyy HH:mm');

export const getLocalDateTimeString = (date: Date): string =>
  format(date, 'dd/MM/yyyy HH:mm:ss');

export const getLocalDateTimeStringSafe = (
  date: string | null | undefined,
  fallback: string,
) => {
  const parsed = parseDateSafe(date);
  return parsed ? getLocalDateTimeString(parsed) : date || fallback;
};

export const getLocalDateString = (date: Date): string =>
  format(date, 'dd/MM/yyyy');

export const parseDateSafe = (date: string | null | undefined): Date | null => {
  if (!date || isNaN(new Date(date).getTime())) {
    return null;
  }
  return new Date(date);
};
