import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';
import {DateRange} from '@angular/material/datepicker';

/**
 * Validates end date is set after start date but does not require any dates to be set.
 * @param control control
 */
export const validOptionalDateRange: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: DateRange<Date> = control.value;
  if (value?.start && value?.end && value.start > value.end) {
    return {required: true};
  }
  return null;
};
