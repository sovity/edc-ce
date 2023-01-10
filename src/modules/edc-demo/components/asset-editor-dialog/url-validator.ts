import {ValidatorFn, Validators} from '@angular/forms';

export const validUrlPattern = /^(http|https):\/\/[^ "]+$/

/**
 * Validates whether control's value is a valid URL.
 * @param control control
 */
export const urlValidator: ValidatorFn = Validators.pattern(validUrlPattern)
