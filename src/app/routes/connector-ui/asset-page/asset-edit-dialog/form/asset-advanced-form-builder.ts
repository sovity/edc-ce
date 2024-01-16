import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {AssetEditorDialogFormValue} from './model/asset-editor-dialog-form-model';

@Injectable()
export class AssetAdvancedFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(
    initial: AssetEditorDialogFormValue['advanced'],
  ): FormGroup<AssetAdvancedFormModel> {
    return this.formBuilder.nonNullable.group({
      dataModel: initial?.dataModel!,
      dataCategory: [initial?.dataCategory || null, Validators.required],
      dataSubcategory: initial?.dataSubcategory || null,
      transportMode: initial?.transportMode || null,
      geoReferenceMethod: initial?.geoReferenceMethod!,
    });
  }
}
