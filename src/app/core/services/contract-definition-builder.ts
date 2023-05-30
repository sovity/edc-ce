import {Injectable} from '@angular/core';
import {ContractDefinitionEditorDialogFormValue} from '../../routes/connector-ui/contract-definition-page/contract-definition-editor-dialog/contract-definition-editor-dialog-form-model';
import {
  ContractDefinitionDto,
  policyDefinitionId,
} from './api/legacy-managent-api-client';
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
      accessPolicyId: policyDefinitionId(formValue.accessPolicy!),
      contractPolicyId: policyDefinitionId(formValue.contractPolicy!),
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
