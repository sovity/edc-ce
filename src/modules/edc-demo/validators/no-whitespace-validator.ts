import {ValidatorFn, Validators} from '@angular/forms';

/**
 * Validates whether value contains whitespaces
 * @param control control
 */
export const noWhitespaceValidator: ValidatorFn = Validators.pattern(/^\S*$/)
