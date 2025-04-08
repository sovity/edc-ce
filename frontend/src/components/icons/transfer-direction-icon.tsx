/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ContractAgreementDirection} from '@/lib/api/client/generated';
import {ArrowBigDownDashIcon, ArrowBigUpDashIcon} from 'lucide-react';

export const TransferDirectionIcon = ({
  direction,
}: {
  direction: ContractAgreementDirection;
}) => {
  return direction === ContractAgreementDirection.Providing ? (
    <ArrowBigUpDashIcon />
  ) : (
    <ArrowBigDownDashIcon />
  );
};
