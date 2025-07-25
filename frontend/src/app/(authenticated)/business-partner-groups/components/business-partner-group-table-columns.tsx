/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import BusinessPartnerGroupActionMenu from './business-partner-group-action-menu';
import type {BusinessPartnerGroupListPageEntry} from '@sovity.de/edc-client';

type ColumnType = ColumnDef<BusinessPartnerGroupListPageEntry>[];

export const useBusinessPartnerGroupTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'groupId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.BusinessPartnerGroupsList.groupId')}
        />
      ),
    },
    {
      accessorKey: 'businessPartnerNumbers',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.BusinessPartnerGroupsList.groupMembers')}
        />
      ),
      cell: ({row}) => {
        return <div>{row.original.businessPartnerNumbers.join(', ')}</div>;
      },
    },
    {
      id: 'actions',
      cell: ({row}) => {
        return <BusinessPartnerGroupActionMenu id={row.original.groupId} />;
      },
    },
  ];
};
