/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useDialogsStore} from '@/lib/stores/dialog-store';
import {Dialog, DialogContent} from './ui/dialog';

const DialogsProvider = () => {
  const {dialogs, dismissDialog} = useDialogsStore();

  const handleOpenChange = (id: string, isOpen: boolean) => {
    if (!isOpen) {
      dismissDialog(id);
    }
  };

  return (
    <>
      {dialogs.map((dialog) => {
        return (
          <Dialog
            key={dialog.id}
            open={dialog.isOpen}
            onOpenChange={(open) => handleOpenChange(dialog.id, open)}>
            <DialogContent className="max-h-[90%]">
              {<dialog.dialogContent />}
            </DialogContent>
          </Dialog>
        );
      })}
    </>
  );
};

export default DialogsProvider;
