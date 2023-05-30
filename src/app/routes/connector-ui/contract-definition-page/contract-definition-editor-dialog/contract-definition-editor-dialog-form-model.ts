import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {PolicyDefinition} from '../../../../core/services/api/legacy-managent-api-client';
import {Asset} from '../../../../core/services/models/asset';

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
  accessPolicy: FormControl<PolicyDefinition | null>;
  contractPolicy: FormControl<PolicyDefinition | null>;
  assets: FormControl<Asset[]>;
}
