/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';
import {DateRange} from '@angular/material/datepicker';

export const validDateRangeOptionalEnd: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: DateRange<Date> = control.value;
  if (!value?.start || (value?.end && value.start > value.end)) {
    return {required: true};
  }
  return null;
};

export const validDateRange: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: DateRange<Date> = control.value;
  if (!value?.start || !value?.end || value.start > value.end) {
    return {required: true};
  }
  return null;
};
