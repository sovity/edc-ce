import {FormControl, UntypedFormGroup, ɵFormGroupValue} from '@angular/forms';

export type PolicyDefinitionCreatePageFormValue =
  ɵFormGroupValue<PolicyDefinitionCreatePageFormModel>;

export interface PolicyDefinitionCreatePageFormModel {
  id: FormControl<string>;
  treeControls: UntypedFormGroup;
}
