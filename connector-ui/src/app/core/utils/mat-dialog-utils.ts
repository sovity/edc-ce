/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {ComponentType} from '@angular/cdk/portal';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {Observable} from 'rxjs';

/**
 * Method for launching Angular Material Dialogs with the lifetime of the dialog being handled by a until$ observable
 *
 * @param dialogService MatDialog
 * @param dialog ComponentType
 * @param config MatDialogConfig
 * @param until$ Observable that controls the lifetime of the dialog
 * @template T Type of the data passed to the dialog
 * @template R Type of the data returned by the dialog
 * @return afterClosed Observable
 */
export function showDialogUntil<T, R>(
  dialogService: MatDialog,
  dialog: ComponentType<any>,
  config: MatDialogConfig<T>,
  until$: Observable<unknown>,
): Observable<R | undefined> {
  const ref = dialogService.open(dialog, config);
  until$.subscribe({
    next: () => ref.close(),
    complete: () => ref.close(),
  });
  return ref.afterClosed();
}
