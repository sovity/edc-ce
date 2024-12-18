import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataOfferFormValidators} from 'src/app/core/validators/data-offer-form-validators';
import {noWhitespacesOrColonsValidator} from '../../../../core/validators/no-whitespaces-or-colons-validator';
import {ExpressionFormControls} from '../../../../shared/business/policy-editor/editor/expression-form-controls';
import {
  PolicyDefinitionCreatePageFormModel,
  PolicyDefinitionCreatePageFormValue,
} from './policy-definition-create-page-form-model';

/**
 * Handles AngularForms for NewPolicyDialog
 */
@Injectable()
export class PolicyDefinitionCreatePageForm {
  group = this.buildFormGroup();

  /**
   * Quick access to full value
   */
  get value(): PolicyDefinitionCreatePageFormValue {
    return this.group.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private expressionFormControls: ExpressionFormControls,
    private validators: DataOfferFormValidators,
  ) {}

  buildFormGroup(): FormGroup<PolicyDefinitionCreatePageFormModel> {
    return this.formBuilder.nonNullable.group({
      id: [
        '',
        [Validators.required, noWhitespacesOrColonsValidator],
        [this.validators.policyIdExistsValidator],
      ],
      treeControls: this.expressionFormControls.formGroup,
    });
  }
}
