import {FormGroup, ɵFormGroupValue} from '@angular/forms';
import {AssetAdvancedFormModel} from './model/asset-advanced-form-model';
import {AssetDatasourceFormModel} from './model/asset-datasource-form-model';
import {AssetMetadataFormModel} from './model/asset-metadata-form-model';

/**
 * Form Model for AssetEditorDialog
 */
export interface AssetEditorDialogFormModel {
  metadata: FormGroup<AssetMetadataFormModel>;
  advanced?: FormGroup<AssetAdvancedFormModel>;
  datasource: FormGroup<AssetDatasourceFormModel>;
}

/**
 * Form Value for AssetEditorDialog
 */
export type AssetEditorDialogFormValue =
  ɵFormGroupValue<AssetEditorDialogFormModel>;
