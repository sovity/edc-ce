/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type ChangeEvent, useState} from 'react';
import {useRouter} from 'next/navigation';
import {Input} from '@/components/ui/input';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {useUpdateTableSearchParamState} from '@/lib/hooks/use-update-table-search-param-state';
import {useUrlParams} from '@/lib/hooks/use-url-params';
import {unsafeCast} from '@/lib/utils/ts-utils';
import {
  type ColumnDef,
  type ColumnSort,
  type FilterFn,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  type Row,
  type SortingState,
  useReactTable,
} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import ExactMatchTooltip from './exact-match-tooltip';
import InternalLink from './links/internal-link';
import {ClientsideDataTablePagination} from './clientside-data-table-pagination';

interface ClientsideDataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  wordFilter: FilterFn<unknown>;
  headerButtonText?: string;
  headerButtonLink?: string;
  headerButtonDataTestId?: string;
  searchInputDataTestId?: string;
  invisibleColumns?: string[];
  filterComponents?: React.ReactNode;
  rowLink?: (row: Row<TData>) => string;
  disableUpdateUrlParams?: boolean;
}

declare module '@tanstack/table-core' {
  interface FilterFns {
    wordFilter: FilterFn<unknown>;
  }
}

/**
 * @deprecated Use `DataTable` instead.
 */
export function ClientsideDataTable<TData, TValue>({
  columns,
  data,
  wordFilter,
  headerButtonText,
  headerButtonLink,
  headerButtonDataTestId,
  searchInputDataTestId,
  invisibleColumns,
  filterComponents,
  rowLink,
  disableUpdateUrlParams,
}: ClientsideDataTableProps<TData, TValue>) {
  const urlParams = useUrlParams();
  const urlSorting = urlParams.sort.map((s) => {
    return unsafeCast<ColumnSort>(JSON.parse(s));
  });

  const [pageIndex, setPageIndex] = useState(urlParams.page - 1);
  const [pageSize, setPageSize] = useState(urlParams.pageSize);
  const [sorting, setSorting] = useState<SortingState>(urlSorting ?? []);
  const [globalFilter, setGlobalFilter] = useState(urlParams.search ?? '');

  const columnVisibility: Record<string, boolean> | undefined =
    invisibleColumns?.reduce(
      (acc, column) => {
        acc[column] = false;
        return acc;
      },
      {} as Record<string, boolean>,
    ) ?? undefined;

  const onSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setGlobalFilter(value);
  };

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    onSortingChange: setSorting,
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    state: {
      sorting,
      globalFilter,
      columnVisibility,
      pagination: {
        pageIndex,
        pageSize,
      },
    },
    onGlobalFilterChange: setGlobalFilter,
    filterFns: {
      wordFilter,
    },
    globalFilterFn: 'wordFilter',
  });

  useUpdateTableSearchParamState(table, disableUpdateUrlParams);

  const router = useRouter();

  const t = useTranslations();

  return (
    <>
      <div className="flex items-center justify-between gap-2">
        <div className="flex w-full items-center gap-2">
          <div className="relative w-1/2">
            <Input
              placeholder={t('General.searchPlaceholder')}
              value={globalFilter}
              onChange={onSearchChange}
              dataTestId={searchInputDataTestId}
            />
            <div className="absolute inset-y-0 right-0 flex items-center pr-2">
              <ExactMatchTooltip />
            </div>
          </div>
          {filterComponents && (
            <div className="flex items-center gap-2">{filterComponents}</div>
          )}
        </div>
        {headerButtonLink && (
          <InternalLink
            href={headerButtonLink}
            dataTestId={headerButtonDataTestId!}>
            {headerButtonText}
          </InternalLink>
        )}
      </div>
      <div className="mt-4 rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext(),
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && 'selected'}
                  className={rowLink ? 'cursor-pointer' : ''}
                  onClick={() => rowLink && router.push(rowLink(row))}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext(),
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center">
                  {t('General.noResults')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="mt-3">
        <ClientsideDataTablePagination
          table={table}
          setPageIndex={setPageIndex}
          setPageSize={setPageSize}
        />
      </div>
    </>
  );
}
