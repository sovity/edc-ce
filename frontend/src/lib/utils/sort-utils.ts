/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type {SortingFn} from '@tanstack/react-table';

export const sortByKey =
  <T>(keyExtractor: (entry: T) => string): SortingFn<T> =>
  (rowA, rowB) => {
    const valueA = keyExtractor(rowA.original).toLowerCase();
    const valueB = keyExtractor(rowB.original).toLowerCase();
    return valueA.localeCompare(valueB);
  };

export const sortByNumericalKey =
  <T>(keyExtractor: (entry: T) => number): SortingFn<T> =>
  (rowA, rowB) => {
    const valueA = keyExtractor(rowA.original);
    const valueB = keyExtractor(rowB.original);
    return valueA - valueB;
  };

export const sortByTimeAgo = <T>(keyExtractor: (entry: T) => Date) =>
  sortByNumericalKey((entry: T) => -keyExtractor(entry).getTime());
