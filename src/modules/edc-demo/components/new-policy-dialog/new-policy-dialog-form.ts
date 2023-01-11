import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NewPolicyDialogFormModel, NewPolicyDialogFormValue, PolicyType} from "./new-policy-dialog-form-model";
import {LanguageSelectItemService} from "../language-select/language-select-item.service";
import {noWhitespaceValidator} from "../../validators/no-whitespace-validator";
import {switchValidation} from "../../utils/form-group-utils";
import {dateRangeRequired} from "../../validators/date-range-required";

/**
 * Handles AngularForms for NewPolicyDialog
 */
@Injectable()
export class NewPolicyDialogForm {

  group = this.buildFormGroup()

  /**
   * Quick access to full value
   */
  get value(): NewPolicyDialogFormValue {
    return this.group.value;
  }

  get policyType(): PolicyType {
    return this.group.controls.policyType.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private languageSelectItemService: LanguageSelectItemService,
  ) {
  }

  buildFormGroup(): FormGroup<NewPolicyDialogFormModel> {
    const formGroup: FormGroup<NewPolicyDialogFormModel> = this.formBuilder.nonNullable.group({
      id: ['', [Validators.required, noWhitespaceValidator]],
      policyType: ['Connector-Restricted-Usage' as PolicyType, Validators.required],
      range: this.formBuilder.group({
        start: null as Date | null,
        end: null as Date | null,
      }),
      connectorId: ''
    });

    // Switch validation depending on selected policy type
    switchValidation({
      formGroup,
      switchCtrl: formGroup.controls.policyType,
      validators: {
        'Connector-Restricted-Usage': {
          connectorId: Validators.required
        },
        'Time-Period-Restricted': {
          'range': dateRangeRequired
        }
      }
    });

    return formGroup;
  }

}
