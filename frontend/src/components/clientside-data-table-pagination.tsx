/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {FilterFn, Table} from '@tanstack/react-table';
import {DataTablePagination} from './data-table-pagination';

interface ClientsideDataTableProps<TData> {
  table: Table<TData>;
}

declare module '@tanstack/table-core' {
  interface FilterFns {
    wordFilter: FilterFn<unknown>;
  }
}

/**
 * @deprecated Use `DataTablePagination` instead.
 */
export function ClientsideDataTablePagination<TData>({
  table,
}: ClientsideDataTableProps<TData>) {
  const totalItems = table.getFilteredRowModel().rows.length;
  const {pageSize, pageIndex} = table.getState().pagination;
  // since this should be one-indexed, need to add 1 in case of a non-empty table
  const pageStart = totalItems === 0 ? 0 : pageIndex * pageSize + 1;
  const pageEnd = Math.min(
    (pageIndex + 1) * pageSize,
    table.getFilteredRowModel().rows.length,
  );
  const nextPage = table.getCanNextPage() ? pageIndex + 1 : undefined;
  const previousPage = table.getCanPreviousPage() ? pageIndex - 1 : undefined;
  const lastPage = Math.max(Math.ceil(totalItems / pageSize) - 1, 0);

  return (
    <DataTablePagination
      tablePage={{
        content: table.getRowModel().rows,
        currentPage: pageIndex,
        pageStart,
        pageEnd,
        totalItems,
        nextPage,
        previousPage,
        pageSize,
        lastPage,
      }}
      setPageIndex={(page) => table.setPageIndex(page)}
      setPageSize={(pageSize) => table.setPageSize(pageSize)}
      tableTestId={'tbl'}
    />
  );
}
