/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {addDays, subDays} from 'date-fns';
import {format} from 'date-fns-tz';

/**
 * Takes the year/month/day information of a local date and creates a new Date object from it.
 * Hour offset context is removed.
 * Can be used to ensure dates are displayed identically across different timezones when stringified in JSON payloads.
 * @param date date to convert
 */
export function toGmtZeroHourDate(date: Date): Date {
  return new Date(format(date, 'yyyy-MM-dd'));
}

export function isMidnightInCurrentTz(date: Date): boolean {
  return format(date, 'HH:mm:ss') === '00:00:00';
}

/**
 * Helper for dealing with the problem:
 *  - Our API does date comparisons based on Date + Time + TZ
 *  - Our UI tries to simplify it to "select full days only"
 *
 * Here we try to reverse the ISO UTC DateTime String to a day, while considering different TZs:
 *  - We accept only midnight in the local tz
 *  - If the date is used for an upper bound, we subtract a day
 */
export function truncateToLocalTzDay(
  date: Date,
  isUpperBound: boolean,
): string {
  date = truncateToLocalTzDayRaw(date, isUpperBound);

  if (isMidnightInCurrentTz(date)) {
    return format(date, 'dd/MM/yyyy');
  }

  // Fallback
  return format(date, 'dd/MM/yyyy HH:mm:ss');
}

export function truncateToLocalTzDayRaw(
  date: Date,
  isUpperBound: boolean,
): Date {
  if (isMidnightInCurrentTz(date) && isUpperBound) {
    // Transform "x <= 2000-01-02 00:00:00" to "x <= 2000-01-01"
    date = subDays(date, 1);
  }
  return date;
}

/**
 * Helper for dealing with the problem:
 *  - Our API does date comparisons based on Date + Time + TZ
 *  - Our UI tries to simplify it to "select full days only"
 *
 * Here we take a local tz "day" and convert it to an ISO UTC DateTime String:
 *  - If the date is used for an upper bound, we add a day
 */
export function localTzDayToIsoString(
  date: Date,
  isUpperBound: boolean,
): string {
  if (isUpperBound) {
    // Transform "x <= 2000-01-01" to "x <= 2000-01-02 00:00:00"
    date = addDays(date, 1);
  }

  return date.toISOString();
}
