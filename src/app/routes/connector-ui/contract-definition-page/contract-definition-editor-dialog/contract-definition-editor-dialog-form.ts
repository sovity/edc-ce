import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {Asset} from '../../../../core/services/models/asset';
import {noWhitespacesOrColonsValidator} from '../../../../core/validators/no-whitespaces-or-colons-validator';
import {
  ContractDefinitionEditorDialogFormModel,
  ContractDefinitionEditorDialogFormValue,
} from './contract-definition-editor-dialog-form-model';

/**
 * Handles AngularForms for ContractDefinitionEditorDialog
 */
@Injectable()
export class ContractDefinitionEditorDialogForm {
  group = this.buildFormGroup();

  /**
   * Quick access to full value
   */
  get value(): ContractDefinitionEditorDialogFormValue {
    return this.group.value;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<ContractDefinitionEditorDialogFormModel> {
    return this.formBuilder.nonNullable.group({
      id: ['', [Validators.required, noWhitespacesOrColonsValidator]],
      accessPolicy: [null as PolicyDefinitionDto | null, Validators.required],
      contractPolicy: [null as PolicyDefinitionDto | null, Validators.required],
      assets: [[] as Asset[], Validators.required],
    });
  }
}
