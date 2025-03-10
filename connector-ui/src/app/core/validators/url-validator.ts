/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export const validUrlPattern = /^(http|https):\/\/[^ "]+$/;

/**
 * Validates whether control's value is a valid URL.
 * @param control control
 */
export const urlValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: string = control.value;

  if (!value?.length || validUrlPattern.test(value)) {
    return null;
  }

  return {url: true};
};
