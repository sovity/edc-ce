/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  type ContractAgreementDirection,
  type ContractTerminationStatus,
} from '@sovity.de/edc-client';

export const canTransfer = (contract: {
  terminationStatus: ContractTerminationStatus;
  direction: ContractAgreementDirection;
}): boolean =>
  contract.terminationStatus === 'ONGOING' &&
  contract.direction === 'CONSUMING';

export const canTerminate = (contract: {
  terminationStatus: ContractTerminationStatus;
}): boolean => contract.terminationStatus === 'ONGOING';
