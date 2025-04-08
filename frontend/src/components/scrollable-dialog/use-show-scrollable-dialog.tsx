/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useDialogsStore} from '@/lib/stores/dialog-store';
import ScrollableDialog, {
  type ScrollableDialogProps,
} from './scrollable-dialog';

const useShowScrollableDialog = (
  dialogId: string,
  data: ScrollableDialogProps,
) => {
  const {showDialog} = useDialogsStore();

  return () => {
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ScrollableDialog
          title={data.title}
          subtitle={data.subtitle}
          content={data.content}
        />
      ),
    });
  };
};

export default useShowScrollableDialog;
