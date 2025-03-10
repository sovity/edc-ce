/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

/**
 * Validates that param string does not contain "=" or "&" characters
 * Temporary solution until EDC double encoding issue is resolved
 * See https://github.com/sovity/edc-extensions/issues/582
 * @param control control
 */
export const validQueryParam: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: string = control.value;
  if (value?.includes('=') || value?.includes('&')) {
    return {invalidQueryParam: true};
  }

  return null;
};
