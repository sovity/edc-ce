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
import {TranslateService} from '@ngx-translate/core';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {NotificationService} from '../../../../core/services/notification.service';
import {AssetDetailDialogDataService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog.service';
import {ConfirmDialogModel} from '../../../../shared/common/confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../../../../shared/common/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../../../shared/common/json-dialog/json-dialog.data';
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
    private translateService: TranslateService,
  ) {}

  onPolicyClick(policyDefinition: PolicyDefinitionDto) {
    const data: JsonDialogData = {
      title: policyDefinition.policyDefinitionId,
      subtitle: 'Policy',
      icon: 'policy',
      objectForJson: JSON.parse(policyDefinition.policy.policyJsonLd),
    };
    this.matDialog.open(JsonDialogComponent, {data});
  }

  onAssetClick(asset: UiAssetMapped) {
    const data = this.assetDetailDialogDataService.assetDetailsReadonly(asset);
    this.assetDetailDialogService
      .open(data, this.ngOnDestroy$)
      .pipe(filter((it) => !!it?.refreshList))
      .subscribe(() => this.deleteDone.emit());
  }

  onContractDefinitionClick(card: ContractDefinitionCard) {
    let dialogRef: MatDialogRef<any>;
    const data: JsonDialogData = {
      title: card.id,
      subtitle: 'Data Offer',
      icon: 'policy',
      objectForJson: card.detailJsonObj,
      toolbarButton: {
        text: 'Delete',
        icon: 'delete',
        confirmation: ConfirmDialogModel.forDelete(
          'general.con_def',
          card.id,
          this.translateService,
        ),
        action: () =>
          this.edcApiService.deleteContractDefinition(card.id).pipe(
            tap(() => {
              this.notificationService.showInfo('Data Offer deleted!');
              this.deleteDone.emit();
              dialogRef?.close();
            }),
            catchError((err) => {
              const msg = `Failed deleting data offer with id ${card.id}`;
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
