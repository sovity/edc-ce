/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type FilterFn, type Row} from '@tanstack/react-table';

export const searchFilter = (search: string, target: string): boolean => {
  const exactStringsMatches = search.match(/"[^"]*"/g) || [];
  const exactStrings = exactStringsMatches.map((it) => {
    search = search.replace(it, '').trim();
    return it.replaceAll('"', '');
  });

  const words = search
    .split(' ')
    .map((it) => it.trim().toLocaleLowerCase())
    .filter((it) => it.length);

  return (
    words.every((x) => target.toLocaleLowerCase().includes(x)) &&
    exactStrings.every((x) => target.includes(x))
  );
};

export const buildWordFilter = (
  colValuesFn: (rowInfo: Row<unknown>) => unknown[],
): FilterFn<unknown> => {
  return (row, _, filterValue) => {
    if (!filterValue || typeof filterValue !== 'string' || filterValue === '') {
      return true;
    }

    const searchedColValues = colValuesFn(row);
    const allValuesStr = searchedColValues.join(' ');

    return searchFilter(filterValue, allValuesStr);
  };
};
