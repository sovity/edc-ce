/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ContractAgreementDirection} from '@/lib/api/client/generated';
import BaseBadge from './base-badge';

function getClasses(direction: ContractAgreementDirection): string {
  switch (direction) {
    case ContractAgreementDirection.Providing:
      return 'bg-cyan-50 text-cyan-700 ring-cyan-600/20';
    case ContractAgreementDirection.Consuming:
      return 'bg-gray-50 text-gray-700 ring-gray-600/20';
  }
}

export const TransferDirectionBadge = ({
  direction,
}: {
  direction: ContractAgreementDirection;
}) => {
  return <BaseBadge label={direction} classes={getClasses(direction)} />;
};
