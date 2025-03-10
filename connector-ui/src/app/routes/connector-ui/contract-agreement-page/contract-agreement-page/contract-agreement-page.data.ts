/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {ContractAgreementPage} from '@sovity.de/edc-client';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';

export type ContractAgreementPageData = Omit<
  ContractAgreementPage,
  'contractAgreements'
> & {
  contractAgreements: ContractAgreementCardMapped[];
  consumingContractAgreements: ContractAgreementCardMapped[];
  providingContractAgreements: ContractAgreementCardMapped[];
  numTotalContractAgreements: number;
};
