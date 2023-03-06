import {Component, Inject, OnDestroy} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Observable, Subject} from 'rxjs';
import {filter, finalize, takeUntil} from 'rxjs/operators';
import {AssetService} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {ContractNegotiationService} from '../../services/contract-negotiation.service';
import {NotificationService} from '../../services/notification.service';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../confirmation-dialog/confirmation-dialog.component';
import {PropertyGridField} from '../property-grid/property-grid-field';
import {AssetDetailDialogData} from './asset-detail-dialog-data';
import {AssetDetailDialogResult} from './asset-detail-dialog-result';
import {AssetPropertyGridBuilder} from './asset-property-grid-builder';

@Component({
  selector: 'edc-demo-asset-detail-dialog',
  templateUrl: './asset-detail-dialog.component.html',
  styleUrls: ['./asset-detail-dialog.component.scss'],
  providers: [AssetPropertyGridBuilder],
})
export class AssetDetailDialog implements OnDestroy {
  asset: Asset;
  props: PropertyGridField[];

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
    public data: AssetDetailDialogData,
    private assetPropertyGridBuilder: AssetPropertyGridBuilder,
    public contractNegotiationService: ContractNegotiationService,
  ) {
    this.asset = this.data.asset;
    this.props = this.assetPropertyGridBuilder.buildPropertyGrid(this.asset);
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
