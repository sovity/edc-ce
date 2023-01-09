import {ValidatorFn, Validators} from '@angular/forms';

/**
 * Validates whether control's value is a valid URL.
 * @param control control
 */
export const urlValidator: ValidatorFn = Validators.pattern(/^(http|https):\/\/[^ "]+$/)
