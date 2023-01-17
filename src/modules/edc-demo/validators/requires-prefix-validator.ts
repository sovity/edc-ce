import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

/**
 * Validates whether control's value starts with given prefix.
 */
export function requiresPrefixValidator(prefix: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (value && !value.startsWith(prefix)) {
      return {requiresPrefix: true};
    }
    return null;
  };
}
