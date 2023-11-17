import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

/**
 * Form Value Type
 */
export type ContractDefinitionEditorDialogFormValue =
  ɵFormGroupValue<ContractDefinitionEditorDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface ContractDefinitionEditorDialogFormModel {
  id: FormControl<string>;
  accessPolicy: FormControl<PolicyDefinitionDto | null>;
  contractPolicy: FormControl<PolicyDefinitionDto | null>;
  assets: FormControl<UiAssetMapped[]>;
}
