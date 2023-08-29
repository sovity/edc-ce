import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {EMPTY, Subject} from 'rxjs';
import {catchError, filter, tap} from 'rxjs/operators';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {ConfirmDialogModel} from '../../../../component-library/confirmation-dialog/confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../../../../component-library/json-dialog/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../../../component-library/json-dialog/json-dialog/json-dialog.data';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {
  PolicyDefinition,
  policyDefinitionId,
} from '../../../../core/services/api/legacy-managent-api-client';
import {Asset} from '../../../../core/services/models/asset';
import {NotificationService} from '../../../../core/services/notification.service';
import {ContractDefinitionCard} from './contract-definition-card';

@Component({
  selector: 'contract-definition-cards',
  templateUrl: './contract-definition-cards.component.html',
})
export class ContractDefinitionCardsComponent implements OnDestroy {
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
    private edcApiService: EdcApiService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private matDialog: MatDialog,
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
    const data = this.assetDetailDialogDataService.assetDetails(asset, false);
    this.assetDetailDialogService
      .open(data, this.ngOnDestroy$)
      .pipe(filter((it) => !!it?.refreshList))
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
          this.edcApiService.deleteContractDefinition(card.id).pipe(
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

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
