import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {EMPTY} from 'rxjs';
import {catchError, filter, map, tap} from 'rxjs/operators';
import {
  ContractDefinitionService,
  PolicyDefinition,
  policyDefinitionId,
} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {NotificationService} from '../../services/notification.service';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';
import {ConfirmDialogModel} from '../confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../json-dialog/json-dialog.component';
import {JsonDialogData} from '../json-dialog/json-dialog.data';
import {ContractDefinitionCard} from './contract-definition-card';

@Component({
  selector: 'edc-demo-contract-definition-cards',
  templateUrl: './contract-definition-cards.component.html',
})
export class ContractDefinitionCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  contractDefinitionCards: ContractDefinitionCard[] = [];

  @Input()
  deleteBusy = false;

  @Output()
  deleteDone = new EventEmitter();

  constructor(
    private matDialog: MatDialog,
    private contractDefinitionService: ContractDefinitionService,
    private notificationService: NotificationService,
  ) {}

  onPolicyClick(policyDefinition: PolicyDefinition) {
    const data: JsonDialogData = {
      title: policyDefinitionId(policyDefinition),
      subtitle: 'Policy',
      icon: 'policy',
      objectForJson: policyDefinition,
    };
    this.matDialog.open(JsonDialogComponent, {data});
  }

  onAssetClick(asset: Asset) {
    const data = AssetDetailDialogData.forAssetDetails(asset, false);
    const ref = this.matDialog.open(AssetDetailDialog, {
      data,
      maxHeight: '90vh',
    });
    ref
      .afterClosed()
      .pipe(
        map((it) => it as AssetDetailDialogResult | null),
        filter((it) => !!it?.refreshList),
      )
      .subscribe(() => this.deleteDone.emit());
  }

  onContractDefinitionClick(card: ContractDefinitionCard) {
    let dialogRef: MatDialogRef<any>;
    const data: JsonDialogData = {
      title: card.id,
      subtitle: 'Contract Definition',
      icon: 'policy',
      objectForJson: card.detailJsonObj,
      actionButton: {
        text: 'Delete',
        color: 'warn',
        confirmation: ConfirmDialogModel.forDelete(
          'contract definition',
          card.id,
        ),
        action: () =>
          this.contractDefinitionService.deleteContractDefinition(card.id).pipe(
            tap(() => {
              this.notificationService.showInfo('Contract Definition deleted!');
              this.deleteDone.emit();
              dialogRef?.close();
            }),
            catchError((err) => {
              const msg = `Failed deleting contract definition with id ${card.id}`;
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
