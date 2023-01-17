import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {PolicyDefinition} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';

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
