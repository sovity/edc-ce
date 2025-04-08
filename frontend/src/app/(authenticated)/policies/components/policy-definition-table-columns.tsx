/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTableColumnHeader} from '@/components/data-column-header';
import {PolicyRenderer} from '@/components/policy-editor/renderer/policy-renderer';
import {type PolicyDefinitionMapped} from '@/app/(authenticated)/policies/components/policy-definition-mapped';
import {type ColumnDef} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import PolicyDefinitionActionMenu from './policy-definition-action-menu';

type ColumnType = ColumnDef<PolicyDefinitionMapped>[];

export const usePolicyDefinitionTableColumns = (): ColumnType => {
  const t = useTranslations();

  return [
    {
      accessorKey: 'policyDefinitionId',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.PolicyList.policyDefinitionId')}
        />
      ),
      cell: ({row}) => {
        return row.original.policyDefinitionId;
      },
    },
    {
      accessorKey: 'expression',
      header: ({column}) => (
        <DataTableColumnHeader
          column={column}
          title={t('Pages.PolicyList.policyExpression')}
        />
      ),
      cell: ({row}) => {
        return (
          <PolicyRenderer
            errors={row.original.errors}
            expression={row.original.expression}
          />
        );
      },
    },
    {
      id: 'actions',
      cell: ({row}) => {
        return <PolicyDefinitionActionMenu policyDefinition={row.original} />;
      },
    },
    // Invisible columns for filtering
    {
      id: 'jsonLd',
      accessorFn: (row) => row.jsonLd,
    },
  ];
};
