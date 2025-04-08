/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type ConnectorLimits} from '@sovity.de/edc-client';
import {contractAgreementPage} from './contract-agreement-fake-service';

export const connectorLimits = (): ConnectorLimits => ({
  numActiveConsumingContractAgreements:
    contractAgreementPage().contractAgreements.filter(
      (it) =>
        it.direction === 'CONSUMING' && it.terminationStatus === 'ONGOING',
    ).length,
  maxActiveConsumingContractAgreements: 1,
});
