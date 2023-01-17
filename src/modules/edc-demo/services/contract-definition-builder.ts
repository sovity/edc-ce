import {Injectable} from '@angular/core';
import {ContractDefinitionDto} from '../../edc-dmgmt-client';
import {ContractDefinitionEditorDialogFormValue} from '../components/contract-definition-editor-dialog/contract-definition-editor-dialog-form-model';
import {AssetProperties} from './asset-properties';

@Injectable({
  providedIn: 'root',
})
export class ContractDefinitionBuilder {
  /**
   * Build {@link ContractDefinitionDto} from {@link ContractDefinitionEditorDialogFormValue}
   *
   * @param formValue form value
   * @return contract definition dto
   */
  buildContractDefinition(
    formValue: ContractDefinitionEditorDialogFormValue,
  ): ContractDefinitionDto {
    return {
      id: formValue.id!.trim(),
      accessPolicyId: formValue.accessPolicy?.id!,
      contractPolicyId: formValue.contractPolicy?.id!,
      criteria: [
        {
          operandLeft: AssetProperties.id,
          operator: 'in',
          operandRight: formValue.assets!.map((it) => it.id),
        },
      ],
    };
  }
}
