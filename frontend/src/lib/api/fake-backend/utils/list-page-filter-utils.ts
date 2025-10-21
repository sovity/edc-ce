/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import type {PaginationRequest} from '@sovity.de/edc-client';

export const filterListPage = <T>(
  searchText: string | undefined,
  pagination: PaginationRequest | undefined,
  originalContent: T[],
  applyQuery: (content: T[], query: string) => T[],
  sort: (content: T[]) => T[],
) => {
  if (searchText) {
    originalContent = applyQuery(originalContent, searchText);
  }
  const totalItems = originalContent.length;
  const currentPage = (pagination?.pageOneBased ?? 1) - 1;
  const pageSize = pagination?.pageSize ?? originalContent.length;
  const totalPages = Math.ceil(totalItems / pageSize);
  const pageStart =
    originalContent.length === 0 ? 0 : currentPage * pageSize + 1;
  const pageEnd =
    pageSize === 0
      ? 0
      : Math.min(pageStart + pageSize - 1, originalContent.length);
  originalContent = sort(originalContent);
  const content = originalContent.slice(pageStart - 1, pageEnd);
  const lastPage = Math.max(totalPages, 1);
  const previousPage = currentPage > 0 ? currentPage : undefined;
  const nextPage = currentPage < lastPage - 1 ? currentPage + 2 : undefined;

  return {
    content,
    pagination: {
      currentPage: currentPage + 1,
      totalItems,
      nextPage,
      previousPage,
      pageStart,
      pageEnd,
      pageSize,
      lastPage,
    },
  };
};
