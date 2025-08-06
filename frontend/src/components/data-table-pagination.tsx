/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  ChevronLeftIcon,
  ChevronRightIcon,
  ChevronsLeftIcon,
  ChevronsRightIcon,
} from 'lucide-react';
import {type TablePage} from './data-table';
import {useEffect} from 'react';

interface DataTablePaginationProps<TData> {
  setPageIndex: (page: number) => unknown;
  setPageSize: (pageSize: number) => unknown;

  tablePage: TablePage<TData>;
}

export function DataTablePagination<TData>({
  setPageIndex,
  setPageSize,
  tableTestId,
  tablePage: {
    totalItems,
    lastPage,
    nextPage,
    previousPage,
    currentPage,
    pageStart,
    pageEnd,
    pageSize,
  },
}: DataTablePaginationProps<TData> & {tableTestId: string}) {
  useEffect(() => {
    if (currentPage > lastPage) {
      setPageIndex(lastPage);
    }
  }, [currentPage, lastPage, setPageIndex]);
  return (
    <div className="flex items-center justify-between px-2">
      <div className="flex items-center space-x-2">
        <p className="text-sm">Rows per page</p>
        <Select
          value={`${pageSize}`}
          onValueChange={(value) => {
            setPageSize(Number(value));
          }}>
          <SelectTrigger className="h-8 w-[70px]">
            <SelectValue placeholder={pageSize} />
          </SelectTrigger>
          <SelectContent side="top">
            {[10, 20, 50, 100].map((pageSize) => (
              <SelectItem key={pageSize} value={`${pageSize}`}>
                {pageSize}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="flex items-center space-x-6 lg:space-x-8">
        <div className="w-full text-right text-sm">
          {pageStart} - {pageEnd} of {totalItems} results
        </div>
        <div className="flex items-center space-x-2">
          <Button
            dataTestId={`btn-first-${tableTestId}`}
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            onClick={() => setPageIndex(0)}
            disabled={currentPage === 0}>
            <span className="sr-only">Go to first page</span>
            <ChevronsLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            dataTestId={`btn-prev-${tableTestId}`}
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => setPageIndex(previousPage!)}
            disabled={previousPage === undefined}>
            <span className="sr-only">Go to previous page</span>
            <ChevronLeftIcon className="h-4 w-4" />
          </Button>
          <Button
            dataTestId={`btn-next-${tableTestId}`}
            variant="outline"
            className="h-8 w-8 p-0"
            onClick={() => setPageIndex(nextPage!)}
            disabled={nextPage === undefined}>
            <span className="sr-only">Go to next page</span>
            <ChevronRightIcon className="h-4 w-4" />
          </Button>
          <Button
            dataTestId={`btn-last-${tableTestId}`}
            variant="outline"
            className="hidden h-8 w-8 p-0 lg:flex"
            onClick={() => setPageIndex(lastPage)}
            disabled={currentPage === lastPage}>
            <span className="sr-only">Go to last page</span>
            <ChevronsRightIcon className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}
