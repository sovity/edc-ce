import {FormGroup, ɵFormGroupValue} from '@angular/forms';
import {AssetAdvancedFormModel} from './asset-advanced-form-model';
import {AssetDatasourceFormModel} from './asset-datasource-form-model';
import {AssetMetadataFormModel} from './asset-metadata-form-model';

/**
 * Form Model for AssetCreateDialog
 */
export interface AssetCreateDialogFormModel {
  metadata: FormGroup<AssetMetadataFormModel>;
  datasource: FormGroup<AssetDatasourceFormModel>;
  advanced?: FormGroup<AssetAdvancedFormModel>;
}

/**
 * Form Value for AssetCreateDialog
 */
export type AssetCreateDialogFormValue =
  ɵFormGroupValue<AssetCreateDialogFormModel>;
