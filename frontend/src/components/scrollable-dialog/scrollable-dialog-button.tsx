/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '../ui/button';
import type {ScrollableDialogProps} from './scrollable-dialog';
import useShowScrollableDialog from './use-show-scrollable-dialog';

export interface ScrollableDialogButton {
  dialogId: string;
  data: ScrollableDialogProps;
  buttonText: string;
}

const ScrollableDialogButton = ({
  dialogId,
  buttonText,
  data,
}: ScrollableDialogButton) => {
  const showScrollableTextDialog = useShowScrollableDialog(dialogId, data);
  return (
    <Button
      dataTestId={'btn-scrollable-dialog-' + dialogId}
      onClick={() => showScrollableTextDialog()}
      variant={'outline'}
      size="fit"
      className="px-3 py-1">
      {buttonText}
    </Button>
  );
};

export default ScrollableDialogButton;
