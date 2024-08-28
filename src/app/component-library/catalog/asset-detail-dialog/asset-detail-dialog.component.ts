import {DOCUMENT} from '@angular/common';
import {Component, Inject, OnDestroy} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Observable, Subject, isObservable} from 'rxjs';
import {filter, finalize, takeUntil} from 'rxjs/operators';
import {UiContractOffer} from '@sovity.de/edc-client';
import {MailtoLinkBuilder} from 'src/app/core/services/mailto-link-builder';
import {EdcApiService} from '../../../core/services/api/edc-api.service';
import {ConnectorLimitsService} from '../../../core/services/connector-limits.service';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {UiAssetMapped} from '../../../core/services/models/ui-asset-mapped';
import {NotificationService} from '../../../core/services/notification.service';
import {ContractAgreementTerminationDialogData} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-termination-dialog/contract-agreement-termination-dialog-data';
import {ContractAgreementTerminationDialogResult} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-termination-dialog/contract-agreement-termination-dialog-result';
import {ContractAgreementTerminationDialogComponent} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-termination-dialog/contract-agreement-termination-dialog.component';
import {ContractAgreementTransferDialogData} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogResult} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-result';
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
  asset!: UiAssetMapped;
  propGroups!: PropertyGridGroup[];

  limitsExceeded: boolean | null = null;

  loading = false;

  get isProgressBarVisible(): boolean {
    switch (this.data.type) {
      case 'data-offer':
        return (
          this.data.dataOffer?.contractOffers?.some((it) =>
            this.contractNegotiationService.isBusy(it),
          ) ?? false
        );
      case 'contract-agreement':
        return this.data.contractAgreement!.isInProgress;
      default:
        return false;
    }
  }

  get isLiveDataOffer(): boolean {
    return (
      this.data.type === 'data-offer' &&
      this.data.asset.dataSourceAvailability === 'LIVE'
    );
  }

  get isOnRequestDataOffer(): boolean {
    return (
      this.data.type === 'data-offer' &&
      this.data.asset.dataSourceAvailability === 'ON_REQUEST'
    );
  }

  constructor(
    private edcApiService: EdcApiService,
    private notificationService: NotificationService,
    private connectorLimitsService: ConnectorLimitsService,
    private matDialog: MatDialog,
    private matDialogRef: MatDialogRef<AssetDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    private _data: AssetDetailDialogData | Observable<AssetDetailDialogData>,
    public contractNegotiationService: ContractNegotiationService,
    private mailtoLinkBuilder: MailtoLinkBuilder,
    @Inject(DOCUMENT) private document: Document,
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
    this.limitsExceeded = data.consumingLimitsExceeded ?? null;
    this.asset = this.data.asset;
    this.propGroups = this.data.propertyGridGroups;
  }

  onContactClick() {
    if (!this.asset.onRequestContactEmail) {
      throw new Error('On request asset must have contact email');
    }

    const url = this.mailtoLinkBuilder.buildMailtoUrl(
      this.asset.onRequestContactEmail,
      this.asset.onRequestContactEmailSubject ??
        "I'm interested in your data offer",
    );
    this.document.location.href = url;
  }

  onEditClick() {
    if (this.data.onAssetEditClick) {
      this.data.onAssetEditClick(this.data.asset, (data) => this.setData(data));
    }
  }

  onDeleteClick() {
    this.confirmDelete().subscribe(() => {
      this.blockingRequest({
        successMessage: `Deleted asset ${this.asset.assetId}.`,
        failureMessage: `Failed deleting asset ${this.asset.assetId}.`,
        onsuccess: () => this.close({refreshList: true}),
        req: () => this.edcApiService.deleteAsset(this.asset.assetId),
      });
    });
  }

  onNegotiateClick(contractOffer: UiContractOffer) {
    this.connectorLimitsService
      .isConsumingAgreementLimitExceeded()
      .subscribe((limitExceeded) => {
        if (!limitExceeded) {
          this.limitsExceeded = false;
          this.contractNegotiationService.negotiate(
            this.data.dataOffer!,
            contractOffer,
          );
        } else {
          this.limitsExceeded = true;
          this.notificationService.showError(
            'Cannot negotiate. Maximum number of active consuming contracts reached.',
          );
        }
      });
  }

  onTransferClick() {
    const data: ContractAgreementTransferDialogData = {
      contractId: this.data.contractAgreement?.contractAgreementId!!,
      asset: this.data.asset,
    };
    const ref = this.matDialog.open(ContractAgreementTransferDialogComponent, {
      data,
    });

    ref
      .afterClosed()
      .subscribe(
        (result: ContractAgreementTransferDialogResult | undefined) => {
          if (result) {
            this.data.refreshCallback?.();
          }
        },
      );
  }

  onTerminateClick() {
    const data: ContractAgreementTerminationDialogData = {
      contractId: this.data.contractAgreement?.contractAgreementId!!,
      asset: this.data.asset,
    };
    const ref = this.matDialog.open(
      ContractAgreementTerminationDialogComponent,
      {data},
    );

    ref
      .afterClosed()
      .subscribe(
        (result: ContractAgreementTerminationDialogResult | undefined) => {
          if (result) {
            this.data.refreshCallback?.();
          }
        },
      );
  }

  private confirmDelete(): Observable<boolean> {
    const dialogData = ConfirmDialogModel.forDelete('asset', this.asset.title);
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
