import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ExpressionFormControls} from '../../../../component-library/policy-editor/editor/expression-form-controls';
import {noWhitespacesOrColonsValidator} from '../../../../core/validators/no-whitespaces-or-colons-validator';
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
  ) {}

  buildFormGroup(): FormGroup<PolicyDefinitionCreatePageFormModel> {
    return this.formBuilder.nonNullable.group({
      id: ['', [Validators.required, noWhitespacesOrColonsValidator]],
      treeControls: this.expressionFormControls.formGroup,
    });
  }
}
