/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type Patcher, patchObj} from '@/lib/utils/object-utils';
import {
  type ContractNegotiationRequest,
  ContractNegotiationSimplifiedState,
  type ContractNegotiationState,
  type UiContractNegotiation,
} from '@sovity.de/edc-client';
import {getAssetById} from './asset-fake-service';
import {addContractAgreement} from './contract-agreement-fake-service';
import {getPolicyDefinitionByJsonLd} from './policy-definition-fake-service';

const initiated: ContractNegotiationState = {
  name: 'INITIATED',
  code: 500,
  simplifiedState: ContractNegotiationSimplifiedState.InProgress,
};

const agreed: ContractNegotiationState = {
  name: 'AGREED',
  code: 1000,
  simplifiedState: ContractNegotiationSimplifiedState.Agreed,
};

let negotiations: UiContractNegotiation[] = [
  {
    contractNegotiationId: 'test-contract-negotiation-1',
    createdAt: new Date(),
    contractAgreementId: 'test-contract-agreement-1',
    state: initiated,
  },
  {
    contractNegotiationId: 'test-contract-negotiation-2',
    createdAt: new Date(),
    contractAgreementId: 'test-contract-agreement-2',
    state: agreed,
  },
];

export const initiateContractNegotiation = (
  request: ContractNegotiationRequest,
): UiContractNegotiation => {
  const contractNegotiationId =
    'dummy-negotiation-' + Math.random().toString().substring(2);
  const negotiation: UiContractNegotiation = {
    contractNegotiationId,
    state: initiated,
    createdAt: new Date(),
  };
  negotiations = [...negotiations, negotiation];

  setTimeout(() => {
    const contractAgreementId =
      'dummy-agreement' + Math.random().toString().substring(2);

    updateNegotiation(contractNegotiationId, () => ({
      state: agreed,
      contractAgreementId,
    }));

    addContractAgreement({
      contractNegotiationId,
      contractAgreementId,
      direction: 'CONSUMING',
      counterPartyAddress: request.counterPartyAddress,
      transferProcesses: [],
      counterPartyId: request.counterPartyId,
      asset: getAssetById(request.assetId)!,
      contractSigningDate: new Date(),
      contractPolicy: getPolicyDefinitionByJsonLd(request.policyJsonLd)!,
      terminationInformation: undefined,
      terminationStatus: 'ONGOING',
    });
  }, 4000);
  return negotiation;
};

export const getContractNegotiation = (id: string): UiContractNegotiation => {
  return negotiations.find((it) => it.contractNegotiationId === id)!;
};

const updateNegotiation = (
  id: string,
  patcher: Patcher<UiContractNegotiation>,
) => {
  negotiations = negotiations.map((it) =>
    it.contractNegotiationId === id ? patchObj(it, patcher) : it,
  );
};
