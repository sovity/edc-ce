import {FormControl, FormGroup, ɵFormGroupValue} from '@angular/forms';
import {AssetEditDialogMode} from '../../asset-edit-dialog-mode';
import {AssetAdvancedFormModel} from './asset-advanced-form-model';
import {AssetDatasourceFormModel} from './asset-datasource-form-model';
import {AssetMetadataFormModel} from './asset-metadata-form-model';

/**
 * Form Model for AssetEditorDialog
 */
export interface AssetEditorDialogFormModel {
  mode: FormControl<AssetEditDialogMode>;
  metadata: FormGroup<AssetMetadataFormModel>;
  advanced?: FormGroup<AssetAdvancedFormModel>;
  datasource?: FormGroup<AssetDatasourceFormModel>;
}

/**
 * Form Value for AssetEditorDialog
 */
export type AssetEditorDialogFormValue =
  ɵFormGroupValue<AssetEditorDialogFormModel>;
