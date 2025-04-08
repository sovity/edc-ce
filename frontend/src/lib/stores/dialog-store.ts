/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {create} from 'zustand';
import {patchItemByKey} from '../utils/patch-item-by-key';

export interface ControlledDialog {
  id: string;
  isOpen: boolean;
  dialogContent: () => React.ReactNode;
}

export type CreateControlledDialog = Omit<ControlledDialog, 'isOpen'>;

interface DialogStore {
  dialogs: ControlledDialog[];
  showDialog: (dialog: CreateControlledDialog) => void;
  dismissDialog: (id: string) => void;
}

export const useDialogsStore = create<DialogStore>((set) => ({
  dialogs: [],
  showDialog: (dialog) => {
    set((prev) => {
      const hasDialog = prev.dialogs.some((d) => d.id === dialog.id);
      if (hasDialog) return {};

      const newDialog: ControlledDialog = {
        ...dialog,
        isOpen: true,
      };

      return {
        dialogs: [...prev.dialogs, newDialog],
      };
    });
  },
  dismissDialog: (id: string) => {
    set((prev) => ({
      dialogs: patchItemByKey(
        prev.dialogs,
        (dialog) => dialog.id,
        id,
        () => ({isOpen: false}),
      ),
    }));
    setTimeout(() => {
      set((prev) => ({
        dialogs: prev.dialogs.filter((dialog) => dialog.id !== id),
      }));
    }, 200);
  },
}));
