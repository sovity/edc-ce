/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import type {ContractAgreementTransferProcess} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {useContractTransferTableColumns} from './contract-transfer-table-columns';

interface ContractTransferHistoryTableProps {
  data: ContractAgreementTransferProcess[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('transferProcessId'),
    row.getValue('state'),
    row.getValue('errorMessage'),
  ];
});

const ContractTransferTable = ({data}: ContractTransferHistoryTableProps) => {
  return (
    <DataTable
      columns={useContractTransferTableColumns()}
      data={data}
      wordFilter={wordFilter}
    />
  );
};

export default ContractTransferTable;
