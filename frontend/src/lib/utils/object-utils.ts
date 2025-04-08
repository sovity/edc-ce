/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export type Patcher<T> = (obj: T) => Partial<T>;

export function patchObj<T>(obj: T, patcher: Patcher<T>): T {
  return {...obj, ...patcher(obj)};
}

export const recordToList = <T>(
  obj?: Record<string, T>,
): {key: string; value: T}[] => {
  if (!obj) {
    return [];
  }
  return Object.entries(obj).map(([key, value]) => ({key, value}));
};

export const removeEmptyStringFields = (
  obj: Record<string, unknown>,
): Record<string, unknown> => {
  return Object.keys(obj)
    .filter((key) => {
      const value = obj[key];
      // Keep only non-empty strings
      return (
        (typeof value === 'string' && value.length > 0) ||
        typeof value !== 'string'
      );
    })
    .reduce((acc: Record<string, unknown>, key: string) => {
      acc[key] = obj[key];
      return acc;
    }, {});
};

export const removeEmpty = <T>(
  obj: Record<string, Array<T>>,
): Record<string, Array<T>> => {
  return Object.keys(obj)
    .filter((key) => obj[key]?.length)
    .reduce((acc: Record<string, Array<T>>, key: string) => {
      acc[key] = obj[key]!;
      return acc;
    }, {});
};

export const objectKeys = <T extends object>(obj: T): Array<keyof T> => {
  return Object.keys(obj) as Array<keyof T>;
};

export const removeEmptyAndDuplicates = <T>(
  obj: Record<string, Array<T>>,
): Record<string, Array<T>> => {
  return Object.keys(obj)
    .filter((key) => obj[key]?.length)
    .reduce((acc: Record<string, Array<T>>, key: string) => {
      acc[key] = [...new Set(obj[key])];
      return acc;
    }, {});
};

export const removeDuplicates = <T>(arr: Array<T>): Array<T> => {
  return [...new Set(arr)];
};

export const withoutKey = <T>(obj: T, key: keyof T): Omit<T, keyof T> => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const {[key]: _, ...rest} = obj;
  return rest;
};

export const patchKey = <T>(
  obj: Record<string, T>,
  key: string,
  patcher: Patcher<T | undefined>,
): Record<string, T> => {
  return {
    ...obj,
    [key]: patchObj(obj[key], patcher),
  } as Record<string, T>;
};
