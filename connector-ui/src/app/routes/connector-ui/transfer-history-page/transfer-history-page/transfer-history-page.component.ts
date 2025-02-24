import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  EMPTY,
  Observable,
  Subject,
  concat,
  interval,
  skip,
  switchMap,
} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {
  TransferHistoryEntry,
  TransferHistoryPage,
  UiAsset,
} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetBuilder} from '../../../../core/services/asset-builder';
import {Fetched} from '../../../../core/services/models/fetched';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {NotificationService} from '../../../../core/services/notification.service';
import {ParticipantIdLocalization} from '../../../../core/services/participant-id-localization';
import {AssetDetailDialogDataService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog.service';
import {JsonDialogService} from '../../../../shared/common/json-dialog/json-dialog.service';

@Component({
  selector: 'transfer-history-page',
  templateUrl: './transfer-history-page.component.html',
  styleUrls: ['./transfer-history-page.component.scss'],
})
export class TransferHistoryPageComponent implements OnInit, OnDestroy {
  columns: string[] = [
    'direction',
    'lastUpdated',
    'assetName',
    'state',
    'counterPartyParticipantId',
    'counterPartyConnectorEndpoint',
    'details',
  ];
  transferProcessesList: Fetched<{
    transferProcesses: Array<TransferHistoryEntry>;
  }> = Fetched.empty();

  constructor(
    private edcApiService: EdcApiService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private assetBuilder: AssetBuilder,
    private notificationService: NotificationService,
    private jsonDialogService: JsonDialogService,
    public participantIdLocalization: ParticipantIdLocalization,
    private translateService: TranslateService,
  ) {}

  onTransferHistoryDetailsClick(item: TransferHistoryEntry) {
    this.jsonDialogService.showJsonDetailDialog(
      {
        title: item.assetName ?? item.assetId,
        subtitle: this.translateService.instant(
          'transfer_history_page.subtitle',
        ),
        icon: 'assignment',
        objectForJson: item,
      },
      this.ngOnDestroy$,
    );
  }

  loadAssetDetails(item: TransferHistoryEntry): Observable<UiAssetMapped> {
    return this.edcApiService
      .getTransferProcessAsset(item.transferProcessId)
      .pipe(map((asset: UiAsset) => this.assetBuilder.buildAsset(asset)));
  }

  onAssetDetailsClick(item: TransferHistoryEntry) {
    this.loadAssetDetails(item).subscribe({
      next: (asset) => {
        const data =
          this.assetDetailDialogDataService.assetDetailsReadonly(asset);
        this.assetDetailDialogService.open(data, this.ngOnDestroy$);
      },
      error: (error) => {
        const message = this.translateService.instant(
          'notification.failed_transfer_detail_fetch',
        );
        console.error(message, error);
        this.notificationService.showError(message);
      },
    });
  }

  ngOnInit(): void {
    this.loadTransferProcesses();
  }

  loadTransferProcesses() {
    const initialRequest: Observable<Fetched<TransferHistoryPage>> =
      this.edcApiService.getTransferHistoryPage().pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching transfer history.',
        }),
      );

    const polling: Observable<Fetched<TransferHistoryPage>> = interval(
      5_000,
    ).pipe(
      skip(1),
      switchMap(() =>
        this.edcApiService
          .getTransferHistoryPage()
          .pipe(catchError(() => EMPTY)),
      ),
      map((data) => Fetched.ready(data)),
    );

    return concat(initialRequest, polling)
      .pipe(
        Fetched.map((transferHistoryPage) => ({
          transferProcesses: transferHistoryPage.transferEntries,
        })),
      )
      .subscribe(
        (transferProcessesList) =>
          (this.transferProcessesList = transferProcessesList),
      );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
