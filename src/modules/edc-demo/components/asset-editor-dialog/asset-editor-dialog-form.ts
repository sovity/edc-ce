import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActiveFeatureSet} from '../../../app/config/active-feature-set';
import {DataCategorySelectItem} from '../data-category-select/data-category-select-item';
import {LanguageSelectItemService} from '../language-select/language-select-item.service';
import {
  AssetEditorDialogFormModel,
  AssetEditorDialogFormValue,
} from './asset-editor-dialog-form-model';
import {DataAddressType} from './data-address-type';
import {AssetAdvancedFormBuilder} from './model/asset-advanced-form-builder';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {AssetDatasourceFormBuilder} from './model/asset-datasource-form-builder';
import {AssetDatasourceFormModel} from './model/asset-datasource-form-model';
import {AssetMetadataFormBuilder} from './model/asset-metadata-form-builder';
import {AssetMetadataFormModel} from './model/asset-metadata-form-model';

/**
 * Handles AngularForms for AssetEditorDialog
 */
@Injectable()
export class AssetEditorDialogForm {
  all = this.buildFormGroup();

  /**
   * FormGroup for stepper step "Metadata"
   */
  metadata = this.all.controls.metadata;

  /**
   * FormGroup for stepper step "Advanced"
   */
  advanced = this.all.controls.advanced!!;

  /**
   * FormGroup for stepper step "Data Source"
   */
  datasource = this.all.controls.datasource;

  /**
   * Quick access to selected data address type
   */
  get dataAddressType(): DataAddressType | null {
    return this.datasource.controls.dataAddressType.value;
  }

  /**
   * Quick access to selected data category
   */
  get dataCategory(): DataCategorySelectItem | null {
    return this.advanced!!.controls.dataCategory.value;
  }

  /**
   * Quick access to full value
   */
  get value(): AssetEditorDialogFormValue {
    return this.all.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private languageSelectItemService: LanguageSelectItemService,
    private activeFeatureSet: ActiveFeatureSet,
    private assetMetadataFormBuilder: AssetMetadataFormBuilder,
    private assetAdvancedFormBuilder: AssetAdvancedFormBuilder,
    private assetDatasourceFormBuilder: AssetDatasourceFormBuilder,
  ) {}

  buildFormGroup(): FormGroup<AssetEditorDialogFormModel> {
    const metadata: FormGroup<AssetMetadataFormModel> =
      this.assetMetadataFormBuilder.buildFormGroup();

    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.assetDatasourceFormBuilder.buildFormGroup();

    const formGroup: FormGroup<AssetEditorDialogFormModel> =
      this.formBuilder.nonNullable.group({
        metadata,
        datasource,
      });

    if (this.activeFeatureSet.hasMdsFields()) {
      const advanced: FormGroup<AssetAdvancedFormModel> =
        this.assetAdvancedFormBuilder.buildFormGroup();
      formGroup.addControl('advanced', advanced);
    }

    return formGroup;
  }

  onHttpHeadersAddClick() {
    this.datasource.controls.httpHeaders.push(
      this.assetDatasourceFormBuilder.buildHeaderFormGroup(),
    );
  }

  onHttpHeadersRemoveClick(index: number) {
    this.datasource.controls.httpHeaders.removeAt(index);
  }
}
