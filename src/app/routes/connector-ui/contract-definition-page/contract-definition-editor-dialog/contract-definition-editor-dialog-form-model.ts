import {FormControl, ɵFormGroupValue} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
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
  accessPolicy: FormControl<PolicyDefinitionDto | null>;
  contractPolicy: FormControl<PolicyDefinitionDto | null>;
  assets: FormControl<Asset[]>;
}
