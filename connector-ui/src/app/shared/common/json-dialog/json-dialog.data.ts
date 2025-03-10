/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Observable} from 'rxjs';
import {ConfirmDialogModel} from '../confirmation-dialog/confirmation-dialog.component';

export interface JsonDialogData {
  title: string;
  subtitle: string;
  icon: string;
  objectForJson: unknown;
  toolbarButton?: DialogToolbarButton;
}

export interface DialogToolbarButton {
  text: string;
  icon: string;
  action: () => Observable<any> | any;
  confirmation?: ConfirmDialogModel;
}
