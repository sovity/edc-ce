/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ContractTerminationStatus} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import BaseBadge from './base-badge';

const getClasses = (terminationStatus: ContractTerminationStatus): string => {
  switch (terminationStatus) {
    case ContractTerminationStatus.Terminated:
      return 'bg-red-50 text-red-700 ring-red-600/20';
    case ContractTerminationStatus.Ongoing:
      return 'bg-gray-50 text-gray-700 ring-gray-600/20';
  }
};

export const TerminationStatusBadge = ({
  terminationStatus,
}: {
  terminationStatus: ContractTerminationStatus;
}) => {
  const t = useTranslations();

  const statusToLabel = (terminationStatus: ContractTerminationStatus) => {
    switch (terminationStatus) {
      case ContractTerminationStatus.Terminated:
        return t('General.terminated').toUpperCase();
      case ContractTerminationStatus.Ongoing:
        return t('General.active').toUpperCase();
    }
  };

  return (
    <BaseBadge
      label={statusToLabel(terminationStatus)}
      classes={getClasses(terminationStatus)}
    />
  );
};
