/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import BaseBadge from '@/components/badges/base-badge';
import {type DataSourceAvailability} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';

export const AssetTypeBadge = ({
  dataSourceAvailability,
}: {
  dataSourceAvailability: DataSourceAvailability;
}) => {
  const t = useTranslations();

  return (
    <BaseBadge
      label={
        dataSourceAvailability === 'ON_REQUEST'
          ? t('General.onRequest')
          : t('General.live')
      }
      classes="bg-gray-50 text-gray-700 ring-gray-600/20"
    />
  );
};

export default AssetTypeBadge;
