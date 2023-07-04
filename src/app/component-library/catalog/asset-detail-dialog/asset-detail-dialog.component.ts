import {Component, Inject, OnDestroy} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Observable, Subject, isObservable} from 'rxjs';
import {filter, finalize, takeUntil} from 'rxjs/operators';
import {ContractNegotiationService} from '../../../core/services/api/contract-negotiation.service';
import {AssetService} from '../../../core/services/api/legacy-managent-api-client';
import {Asset} from '../../../core/services/models/asset';
import {NotificationService} from '../../../core/services/notification.service';
import {ContractAgreementTransferDialogData} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogComponent} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog.component';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../../confirmation-dialog/confirmation-dialog/confirmation-dialog.component';
import {PropertyGridGroup} from '../../property-grid/property-grid-group/property-grid-group';
import {AssetDetailDialogData} from './asset-detail-dialog-data';
import {AssetDetailDialogResult} from './asset-detail-dialog-result';

/**
 * Asset Detail Dialog
 * Contract Agreement Detail Dialog
 * Contract Offer Detail Dialog
 * <p>
 * All in one! If that's a good idea remains to be seen.
 */
@Component({
  selector: 'asset-detail-dialog',
  templateUrl: './asset-detail-dialog.component.html',
  styleUrls: ['./asset-detail-dialog.component.scss'],
})
export class AssetDetailDialogComponent implements OnDestroy {
  data!: AssetDetailDialogData;
  asset!: Asset;
  propGroups!: PropertyGridGroup[];

  loading = false;

  get negotiationState(): 'ready' | 'negotiating' | 'negotiated' {
    const contractOffer = this.data.contractOffer!;
    if (this.contractNegotiationService.isNegotiated(contractOffer)) {
      return 'negotiated';
    } else if (this.contractNegotiationService.isBusy(contractOffer)) {
      return 'negotiating';
    }
    return 'ready';
  }

  constructor(
    private notificationService: NotificationService,
    private assetService: AssetService,
    private matDialog: MatDialog,
    private matDialogRef: MatDialogRef<AssetDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    private _data: AssetDetailDialogData | Observable<AssetDetailDialogData>,
    public contractNegotiationService: ContractNegotiationService,
  ) {
    if (isObservable(this._data)) {
      this._data
        .pipe(takeUntil(this.ngOnDestroy$))
        .subscribe((data) => this.setData(data));
    } else {
      this.setData(this._data);
    }
  }

  setData(data: AssetDetailDialogData) {
    this.data = data;
    this.asset = this.data.asset;
    this.propGroups = this.data.propertyGridGroups;
  }

  onDeleteClick() {
    this.confirmDelete().subscribe(() => {
      this.blockingRequest({
        successMessage: `Deleted asset ${this.asset.id}.`,
        failureMessage: `Failed deleting asset ${this.asset.id}.`,
        onsuccess: () => this.close({refreshList: true}),
        req: () => this.assetService.removeAsset(this.asset.id),
      });
    });
  }

  onNegotiateClick() {
    this.contractNegotiationService.negotiate(this.data.contractOffer!);
  }

  onTransferClick() {
    const data: ContractAgreementTransferDialogData = {
      contractId: this.data.contractAgreement?.contractAgreementId!!,
      asset: this.data.asset,
    };
    this.matDialog.open(ContractAgreementTransferDialogComponent, {
      data,
    });
  }

  private confirmDelete(): Observable<boolean> {
    const dialogData = ConfirmDialogModel.forDelete(
      'asset',
      `"${this.asset.name}"`,
    );
    const ref = this.matDialog.open(ConfirmationDialogComponent, {
      maxWidth: '20%',
      data: dialogData,
    });
    return ref.afterClosed().pipe(filter((it) => !!it));
  }

  private blockingRequest<T>(opts: {
    req: () => Observable<T>;
    successMessage: string;
    failureMessage: string;
    onsuccess?: () => void;
  }) {
    if (this.loading) {
      return;
    }

    this.loading = true;
    opts
      .req()
      .pipe(
        takeUntil(this.ngOnDestroy$),
        finalize(() => (this.loading = false)),
      )
      .subscribe({
        complete: () => {
          this.notificationService.showInfo(opts.successMessage);
          if (opts.onsuccess) {
            opts.onsuccess();
          }
        },
        error: (err) => {
          console.error(opts.failureMessage, err);
          this.notificationService.showError(opts.failureMessage);
        },
      });
  }

  private close(result: AssetDetailDialogResult) {
    this.matDialogRef.close(result);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
