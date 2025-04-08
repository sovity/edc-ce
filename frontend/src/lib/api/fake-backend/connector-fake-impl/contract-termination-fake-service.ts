/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  type IdResponseDto,
  type TerminateContractAgreementRequest,
} from '@sovity.de/edc-client';
import {updateAgreement} from './contract-agreement-fake-service';

export const initiateContractTermination = (
  request: TerminateContractAgreementRequest,
): IdResponseDto => {
  const response: IdResponseDto = {
    id: request.contractAgreementId,
    lastUpdatedDate: new Date(),
  };

  updateAgreement(request.contractAgreementId, () => ({
    terminationStatus: 'TERMINATED',
    terminationInformation: {
      terminatedAt: new Date(),
      terminatedBy: 'SELF',
      reason: request.contractTerminationRequest?.reason ?? '',
      detail: request.contractTerminationRequest?.detail ?? '',
    },
  }));
  return response;
};
