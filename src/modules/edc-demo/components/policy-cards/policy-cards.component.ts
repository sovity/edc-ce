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
import {PolicyService} from '../../../edc-dmgmt-client';
import {NotificationService} from '../../services/notification.service';
import {ConfirmDialogModel} from '../confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../json-dialog/json-dialog.component';
import {JsonDialogData} from '../json-dialog/json-dialog.data';
import {PolicyCard} from './policy-card';

@Component({
  selector: 'edc-demo-policy-cards',
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
    private matDialog: MatDialog,
    private notificationService: NotificationService,
    private policyService: PolicyService,
  ) {}

  onPolicyDetailClick(policyCard: PolicyCard) {
    let dialogRef: MatDialogRef<any>;
    const data: JsonDialogData = {
      title: policyCard.id,
      subtitle: 'Policy',
      icon: 'policy',
      objectForJson: policyCard.objectForJson,
      actionButton: {
        text: 'Delete',
        color: 'warn',
        confirmation: ConfirmDialogModel.forDelete('policy', policyCard.id),
        action: () =>
          this.policyService.deletePolicy(policyCard.id).pipe(
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
