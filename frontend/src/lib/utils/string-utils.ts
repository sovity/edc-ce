/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export const getInitials = (name: string): string => {
  const parts = name.split(' ');

  if (!parts.length) {
    return '';
  }

  if (parts.length === 1) {
    return parts[0]!.substring(0, 2).toUpperCase();
  }

  return (
    parts[0]!.charAt(0).toUpperCase() +
    parts[parts.length - 1]!.charAt(0).toUpperCase()
  );
};

export const toKebabCase = (str: string): string => {
  return str.toLowerCase().replace(/\s/g, '-');
};

export function trimOrEmpty(s: string | null | undefined): string {
  return s?.trim() ?? '';
}

export function everythingBefore(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? s : s.substring(0, index);
}

export function everythingAfter(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? '' : s.substring(index + separator.length);
}
