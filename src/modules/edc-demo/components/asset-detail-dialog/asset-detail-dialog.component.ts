import {Component, Inject, OnDestroy} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Observable, Subject, isObservable} from 'rxjs';
import {filter, finalize, takeUntil} from 'rxjs/operators';
import {AssetService} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {ContractNegotiationService} from '../../services/contract-negotiation.service';
import {NotificationService} from '../../services/notification.service';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../confirmation-dialog/confirmation-dialog.component';
import {ContractAgreementTransferDialogData} from '../contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialog} from '../contract-agreement-transfer-dialog/contract-agreement-transfer-dialog.component';
import {PropertyGridGroup} from '../property-grid-group/property-grid-group';
import {AssetDetailDialogData} from './asset-detail-dialog-data';
import {AssetDetailDialogResult} from './asset-detail-dialog-result';
import {AssetPropertyGridGroupBuilder} from './asset-property-grid-group-builder';

/**
 * Asset Detail Dialog
 * Contract Agreement Detail Dialog
 * Contract Offer Detail Dialog
 * <p>
 * All in one! If that's a good idea remains to be seen.
 */
@Component({
  selector: 'edc-demo-asset-detail-dialog',
  templateUrl: './asset-detail-dialog.component.html',
  styleUrls: ['./asset-detail-dialog.component.scss'],
  providers: [AssetPropertyGridGroupBuilder],
})
export class AssetDetailDialog implements OnDestroy {
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
    private matDialogRef: MatDialogRef<AssetDetailDialog>,
    @Inject(MAT_DIALOG_DATA)
    private _data: AssetDetailDialogData | Observable<AssetDetailDialogData>,
    private assetPropertyGridGroupBuilder: AssetPropertyGridGroupBuilder,
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
    this.propGroups =
      this.assetPropertyGridGroupBuilder.buildPropertyGridGroups(
        this.data.asset,
        this.data.contractAgreement,
        this.data.policy,
      );
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
    };
    const dialogRef = this.matDialog.open(ContractAgreementTransferDialog, {
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
