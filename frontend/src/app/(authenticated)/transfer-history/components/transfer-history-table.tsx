/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataTable} from '@/components/data-table';
import type {TransferHistoryEntry} from '@/lib/api/client/generated';
import {buildWordFilter} from '@/lib/utils/build-word-filter';
import {useTransferHistoryTableColumns} from './transfer-history-table-columns';
import {urls} from '@/lib/urls';

interface TransferHistoryTableProps {
  data: TransferHistoryEntry[];
}

const wordFilter = buildWordFilter((row) => {
  return [
    row.getValue('assetName'),
    row.getValue('assetId'),
    row.getValue('direction'),
    row.getValue('state'),
    row.getValue('counterPartyParticipantId'),
    row.getValue('counterPartyConnectorEndpoint'),
    row.getValue('errorMessage'),
  ];
});

const invisibleColumns = [
  'assetId',
  'direction',
  'counterPartyParticipantId',
  'counterPartyConnectorEndpoint',
  'errorMessage',
];

const TransferHistoryTable = ({data}: TransferHistoryTableProps) => {
  return (
    <DataTable
      columns={useTransferHistoryTableColumns()}
      data={data}
      wordFilter={wordFilter}
      invisibleColumns={invisibleColumns}
      rowLink={(row) =>
        urls.contracts.detailPage(row.original.contractAgreementId)
      }
    />
  );
};

export default TransferHistoryTable;
