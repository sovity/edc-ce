/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type ContractAgreementCard} from '@sovity.de/edc-client';

export const canTransfer = (contract: ContractAgreementCard): boolean =>
  contract.terminationStatus === 'ONGOING' &&
  contract.direction === 'CONSUMING';

export const canTerminate = (contract: ContractAgreementCard): boolean =>
  contract.terminationStatus === 'ONGOING';
