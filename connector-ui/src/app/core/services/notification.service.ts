/*
 * Copyright 2022 Eclipse Foundation and Contributors
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 */
import {Injectable} from '@angular/core';
import {
  MatSnackBar,
  MatSnackBarConfig,
  MatSnackBarDismiss,
} from '@angular/material/snack-bar';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  /**
   * Shows a snackbar message with a particular text
   * @param message The text to display
   * @param action A string specifying the text on an action button. If left out, no action button is shown.
   * If left out, and onAction is specified, "Done" is used as default.
   * @param onAction A callback that is invoked when the action button is clicked.
   */
  public showInfo(
    message: string,
    action?: string,
    onAction?: () => any,
  ): Observable<MatSnackBarDismiss> {
    if (!action && onAction) {
      action = 'Done';
    }
    const config: MatSnackBarConfig = {
      duration: onAction ? 5000 : 3000, // no auto-cancel if an action was specified
      verticalPosition: 'top',
      politeness: 'polite',
      horizontalPosition: 'center',
      panelClass: ['snackbar-info-style'], //see styles.scss
    };
    const ref = this.snackBar.open(message, action, config);

    if (onAction) {
      ref.onAction().subscribe(() => onAction());
    }

    return ref.afterDismissed();
  }

  public showError(message: string): Observable<MatSnackBarDismiss> {
    const config: MatSnackBarConfig = {
      duration: 5000, // no auto-cancel if an action was specified
      verticalPosition: 'top',
      politeness: 'assertive',
      horizontalPosition: 'center',
      panelClass: ['snackbar-error-style'], //see styles.scss
    };

    const ref = this.snackBar.open(message, undefined, config);
    return ref.afterDismissed();
  }
}
