/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import hash from 'object-hash';
import {create} from 'zustand';

export interface BreadcrumbItem {
  label: string;
  href?: string;
}

interface BreadcrumbStore {
  items: BreadcrumbItem[];
  setItems: (items: BreadcrumbItem[]) => void;
}

export const useBreadcrumbStore = create<BreadcrumbStore>((set) => ({
  items: [],
  setItems: (items) => {
    set((prev) => {
      if (hash(prev.items) === hash(items)) {
        return prev;
      }
      return {items};
    });
  },
}));
