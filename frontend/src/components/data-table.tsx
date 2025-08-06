/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
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
import {useUrlParams} from '@/lib/hooks/use-url-params';
import {
  type ColumnDef,
  type ColumnSort,
  flexRender,
  getCoreRowModel,
  type Row,
  type SortingState,
  useReactTable,
} from '@tanstack/react-table';
import {useTranslations} from 'next-intl';
import ExactMatchTooltip from './exact-match-tooltip';
import InternalLink from './links/internal-link';
import {useUpdateTableSearchParamState} from '@/lib/hooks/use-update-table-search-param-state';
import {unsafeCast} from '@/lib/utils/ts-utils';
import {DataTablePagination} from './data-table-pagination';
import {useQuery} from '@tanstack/react-query';
import type {AssetListPage} from '@sovity.de/edc-client';

export type TablePage<TData> = Omit<AssetListPage, 'content'> & {
  content: TData[];
};

export type TableFilterParams = {
  query?: string;
  page?: number;
  pageSize?: number;
  sorting?: SortingState;
};

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  buildDataKey: (params: TableFilterParams) => (string | number)[];
  getData: (params: TableFilterParams) => Promise<TablePage<TData>>;
  headerButtonText?: string;
  headerButtonLink?: string;
  headerButtonDataTestId?: string;
  searchInputDataTestId?: string;
  rowLink?: (row: Row<TData>) => string;
  disableUpdateUrlParams?: boolean;
}

export function DataTable<TData, TValue>({
  columns,
  buildDataKey,
  getData,
  headerButtonText,
  headerButtonLink,
  headerButtonDataTestId,
  searchInputDataTestId,
  rowLink,
  disableUpdateUrlParams,
}: DataTableProps<TData, TValue>) {
  const urlParams = useUrlParams();
  const urlSorting = urlParams.sort.map((s) => {
    return unsafeCast<ColumnSort>(JSON.parse(s));
  });

  const [sorting, setSorting] = useState<SortingState>(urlSorting ?? []);
  const [query, setQuery] = useState(urlParams.search ?? '');
  const [page, setPageIndex] = useState(urlParams.page);
  const [pageSize, setPageSize] = useState(urlParams.pageSize);

  const {data} = useQuery(
    buildDataKey({query, page, pageSize, sorting}),
    () => getData({query, page, pageSize, sorting}),
    {
      keepPreviousData: true,
    },
  );

  const table = useReactTable({
    data: data?.content ?? [],
    columns,
    getCoreRowModel: getCoreRowModel(),
    onSortingChange: setSorting,
    state: {
      globalFilter: query,
      sorting,
      pagination: {
        pageIndex: page,
        pageSize,
      },
    },
    filterFns: {
      wordFilter: () => false,
    },
    manualFiltering: true,
    manualSorting: true,
    manualPagination: true,
  });
  useUpdateTableSearchParamState(table, disableUpdateUrlParams);

  const router = useRouter();

  const t = useTranslations();

  return (
    <div>
      <div className="flex items-center justify-between gap-2">
        <div className="flex w-full items-center gap-2">
          <div className="relative w-1/2">
            <Input
              placeholder={t('General.searchPlaceholder')}
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              dataTestId={searchInputDataTestId}
            />
            <div className="absolute inset-y-0 right-0 flex items-center pr-2">
              <ExactMatchTooltip />
            </div>
          </div>
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
      <div className="mt-3"></div>
      {data && (
        <DataTablePagination
          tablePage={data}
          setPageIndex={(page) => setPageIndex(page)}
          setPageSize={(pageSize) => setPageSize(pageSize)}
          tableTestId={'tbl'}
        />
      )}
    </div>
  );
}
