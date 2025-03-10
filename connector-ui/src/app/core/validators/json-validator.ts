/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

/**
 * Validates whether control's value is valid JSON.
 * @param control control
 */
export const jsonValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value = control.value;
  if (value) {
    try {
      JSON.parse(value);
    } catch (e) {
      return {jsonInvalid: true};
    }
  }
  return null;
};
