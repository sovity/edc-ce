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

  if (!value.length || validUrlPattern.test(value)) {
    return null;
  }

  return {url: true};
};
