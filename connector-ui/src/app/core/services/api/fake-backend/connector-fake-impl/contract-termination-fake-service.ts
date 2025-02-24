import {
  ContractAgreementCard,
  IdResponseDto,
  TerminateContractAgreementRequest,
} from '@sovity.de/edc-client';
import {updateAgreement} from './contract-agreement-fake-service';

export const initiateContractTermination = (
  request: TerminateContractAgreementRequest,
): IdResponseDto => {
  let response: IdResponseDto = {
    id: request.contractAgreementId,
    lastUpdatedDate: new Date(),
  };

  updateAgreement(
    request.contractAgreementId,
    (agremeent: ContractAgreementCard) => ({
      terminationStatus: 'TERMINATED',
      terminationInformation: {
        terminatedAt: new Date(),
        terminatedBy: 'SELF',
        reason: request.contractTerminationRequest?.reason ?? '',
        detail: request.contractTerminationRequest?.detail ?? '',
      },
    }),
  );
  return response;
};
