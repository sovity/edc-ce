/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
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
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {EMPTY} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ConfirmDialogModel} from '../../../../shared/common/confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../../../../shared/common/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../../../shared/common/json-dialog/json-dialog.data';
import {PolicyCard} from './policy-card';

@Component({
  selector: 'policy-cards',
  templateUrl: './policy-cards.component.html',
})
export class PolicyCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  policyCards: PolicyCard[] = [];

  @Input()
  deleteBusy = false;

  @Output()
  deleteDone = new EventEmitter();

  constructor(
    private edcApiService: EdcApiService,
    private matDialog: MatDialog,
    private notificationService: NotificationService,
    private translateService: TranslateService,
  ) {}

  onPolicyDetailClick(policyCard: PolicyCard) {
    let dialogRef: MatDialogRef<any>;
    const data: JsonDialogData = {
      title: policyCard.id,
      subtitle: this.translateService.instant('general.policy'),
      icon: 'policy',
      objectForJson: policyCard.objectForJson,
      toolbarButton: {
        text: this.translateService.instant('general.delete'),
        icon: 'delete',
        confirmation: ConfirmDialogModel.forDelete(
          'general.policy',
          policyCard.id,
          this.translateService,
        ),
        action: () =>
          this.edcApiService.deletePolicyDefinition(policyCard.id).pipe(
            tap(() => {
              this.notificationService.showInfo('Policy deleted!');
              this.deleteDone.emit();
              dialogRef?.close();
            }),
            catchError((err) => {
              const msg = `Failed deleting policy with id ${policyCard.id}`;
              console.error(msg, err);
              this.notificationService.showError(msg);
              return EMPTY;
            }),
          ),
      },
    };

    dialogRef = this.matDialog.open(JsonDialogComponent, {data});
  }
}
