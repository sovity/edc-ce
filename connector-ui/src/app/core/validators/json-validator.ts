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
