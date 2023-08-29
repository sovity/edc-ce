import {Injectable} from '@angular/core';
import {UiCriterionLiteralDtoTypeEnum} from '@sovity.de/edc-client';
import {ContractDefinitionRequest} from '@sovity.de/edc-client/dist/generated/models/ContractDefinitionRequest';
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
  ): ContractDefinitionRequest {
    return {
      contractDefinitionId: formValue.id ?? '',
      accessPolicyId: policyDefinitionId(formValue.accessPolicy!),
      contractPolicyId: policyDefinitionId(formValue.contractPolicy!),
      assetSelector: [
        {
          operandLeft: AssetProperties.id,
          operator: 'IN',
          operandRight: {
            type: UiCriterionLiteralDtoTypeEnum.ValueList,
            valueList: formValue.assets!.map((it) => it.id),
          },
        },
      ],
    };
  }
}
