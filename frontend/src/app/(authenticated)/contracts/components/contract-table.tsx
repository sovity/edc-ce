/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import {DataTable} from '@/components/data-table';
import {Button} from '@/components/ui/button';
import type {ContractAgreementCard} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {cn} from '@/lib/utils/css-utils';
import {useTranslations} from 'next-intl';
import {useContractTableColumns} from './contract-table-columns';
import {urls} from '@/lib/urls';

interface ContractListTableProps {
  data: ContractAgreementCard[];
}

type TerminationFilterValues = 'TERMINATED' | 'ONGOING' | 'ALL';
type DirectionFilterValues = 'PROVIDING' | 'CONSUMING' | 'ALL';

const ContractTable = ({data}: ContractListTableProps) => {
  const t = useTranslations();
  const [terminationFilter, setTerminationFilter] =
    useState<TerminationFilterValues>('ONGOING');
  const [directionFilter, setDirectionFilter] =
    useState<DirectionFilterValues>('ALL');

  const filterData = () => {
    return data.filter((card) => {
      if (
        terminationFilter === 'TERMINATED' &&
        card.terminationStatus !== 'TERMINATED'
      ) {
        return false;
      }
      if (
        terminationFilter === 'ONGOING' &&
        card.terminationStatus !== 'ONGOING'
      ) {
        return false;
      }

      if (directionFilter === 'PROVIDING' && card.direction !== 'PROVIDING') {
        return false;
      }
      if (directionFilter === 'CONSUMING' && card.direction !== 'CONSUMING') {
        return false;
      }

      return true;
    });
  };

  const wordFilter = buildWordFilter((row) => {
    return [
      row.getValue('assetId'),
      row.getValue('contractAgreementId'),
      row.getValue('counterPartyAddress'),
      row.getValue('creatorOrganizationName'),
    ];
  });

  const invisibleColumns = ['contractAgreementId', 'creatorOrganizationName'];

  const terminationFilterButtons = () => {
    const options: {value: TerminationFilterValues; label: string}[] = [
      {value: 'ALL', label: t('General.all')},
      {value: 'ONGOING', label: t('General.active')},
      {value: 'TERMINATED', label: t('General.terminated')},
    ];

    return (
      <div className="inline-flex rounded-md shadow-sm" role="group">
        {options.map((option) => (
          <Button
            dataTestId={`btn-termination-filter-${option.value}`}
            key={option.value}
            onClick={() => setTerminationFilter(option.value)}
            className={cn(
              option.value === 'ALL' && 'rounded-l-md rounded-r-none',
              option.value === 'ONGOING' &&
                'rounded-none border-l-0 border-r-0',
              option.value === 'TERMINATED' && 'rounded-l-none rounded-r-md',
              'hover:bg-primary/85 hover:text-primary-foreground',
              terminationFilter === option.value
                ? 'bg-primary/85 text-primary-foreground'
                : 'bg-background text-muted-foreground',
            )}
            variant="outline"
            size="sm">
            {option.label}
          </Button>
        ))}
      </div>
    );
  };

  const directionFilterButtons = () => {
    const options: {value: DirectionFilterValues; label: string}[] = [
      {value: 'ALL', label: t('General.all')},
      {value: 'PROVIDING', label: t('General.providing')},
      {value: 'CONSUMING', label: t('General.consuming')},
    ];

    return (
      <div className="ml-2 inline-flex rounded-md shadow-sm" role="group">
        {options.map((option) => (
          <Button
            key={option.value}
            dataTestId={`btn-direction-filter-${option.value}`}
            onClick={() => setDirectionFilter(option.value)}
            className={cn(
              option.value === 'ALL' && 'rounded-l-md rounded-r-none',
              option.value === 'PROVIDING' &&
                'rounded-none border-l-0 border-r-0',
              option.value === 'CONSUMING' && 'rounded-l-none rounded-r-md',
              'hover:bg-primary/85 hover:text-primary-foreground',
              directionFilter === option.value
                ? 'bg-primary/85 text-primary-foreground'
                : 'bg-background text-muted-foreground',
            )}
            variant="outline"
            size="sm">
            {option.label}
          </Button>
        ))}
      </div>
    );
  };

  const combinedFilterComponents = (
    <div className="flex flex-row items-center gap-2">
      {terminationFilterButtons()}
      {directionFilterButtons()}
    </div>
  );

  return (
    <DataTable
      columns={useContractTableColumns()}
      data={filterData()}
      wordFilter={wordFilter}
      filterComponents={combinedFilterComponents}
      invisibleColumns={invisibleColumns}
      rowLink={(row) =>
        urls.contracts.detailPage(row.original.contractAgreementId)
      }
    />
  );
};

export default ContractTable;
