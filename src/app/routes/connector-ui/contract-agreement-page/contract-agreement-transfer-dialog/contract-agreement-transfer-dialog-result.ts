import {TransferId} from '../../../../core/services/api/legacy-managent-api-client';

export interface ContractAgreementTransferDialogResult {
  transferProcessId: TransferId;
  contractId: string;
}
