import {FormControl, FormGroup, ɵFormGroupValue} from '@angular/forms';
import {AssetAdvancedFormModel} from './asset-advanced-form-model';
import {AssetDatasourceFormModel} from './asset-datasource-form-model';
import {AssetEditDialogMode} from './asset-edit-dialog-mode';
import {AssetGeneralFormModel} from './asset-general-form-model';

/**
 * Form Model for Edit Asset Form
 */
export interface EditAssetFormModel {
  mode: FormControl<AssetEditDialogMode>;
  general: FormGroup<AssetGeneralFormModel>;
  datasource: FormGroup<AssetDatasourceFormModel>;
  advanced?: FormGroup<AssetAdvancedFormModel>;
}

/**
 * Form Value for Edit Asset Form
 */
export type EditAssetFormValue = ɵFormGroupValue<EditAssetFormModel>;
