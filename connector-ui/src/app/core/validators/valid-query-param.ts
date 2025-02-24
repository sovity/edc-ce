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
