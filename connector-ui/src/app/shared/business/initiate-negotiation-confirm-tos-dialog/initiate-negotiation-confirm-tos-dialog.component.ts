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
import {Component} from '@angular/core';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-initiate-negotiation-confirm-tos-dialog',
  templateUrl: './initiate-negotiation-confirm-tos-dialog.component.html',
  styleUrls: ['./initiate-negotiation-confirm-tos-dialog.component.scss'],
})
export class InitiateNegotiationConfirmTosDialogComponent {
  checkboxChecked = false;

  constructor(
    public dialogRef: MatDialogRef<InitiateNegotiationConfirmTosDialogComponent>,
  ) {}

  public onCheckboxChange($event: MatCheckboxChange) {
    this.checkboxChecked = $event.checked;
  }

  onCancel() {
    this.dialogRef.close(false);
  }

  onConfirm() {
    if (this.checkboxChecked) {
      this.dialogRef.close(true);
    }
  }
}
