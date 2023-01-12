import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {
  ContractDefinitionEditorDialogFormModel,
  ContractDefinitionEditorDialogFormValue
} from "./contract-definition-editor-dialog-form-model";
import {noWhitespaceValidator} from "../../validators/no-whitespace-validator";
import {PolicyDefinition} from "../../../edc-dmgmt-client";
import {Asset} from "../../models/asset";

/**
 * Handles AngularForms for ContractDefinitionEditorDialog
 */
@Injectable()
export class ContractDefinitionEditorDialogForm {

  group = this.buildFormGroup()

  /**
   * Quick access to full value
   */
  get value(): ContractDefinitionEditorDialogFormValue {
    return this.group.value;
  }

  constructor(
    private formBuilder: FormBuilder,
  ) {
  }

  buildFormGroup(): FormGroup<ContractDefinitionEditorDialogFormModel> {
    return this.formBuilder.nonNullable.group({
      id: ['', [Validators.required, noWhitespaceValidator]],
      accessPolicy: [null as PolicyDefinition | null, Validators.required],
      contractPolicy: [null as PolicyDefinition | null, Validators.required],
      assets: [[] as Asset[], Validators.required]
    });
  }

}
