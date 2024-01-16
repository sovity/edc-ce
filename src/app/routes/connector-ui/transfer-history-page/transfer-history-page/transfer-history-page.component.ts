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
import {
  TransferHistoryEntry,
  TransferHistoryPage,
  UiAsset,
} from '@sovity.de/edc-client';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {JsonDialogService} from '../../../../component-library/json-dialog/json-dialog/json-dialog.service';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetBuilder} from '../../../../core/services/asset-builder';
import {Fetched} from '../../../../core/services/models/fetched';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {NotificationService} from '../../../../core/services/notification.service';
import {ParticipantIdLocalization} from '../../../../core/services/participant-id-localization';

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
  ) {}

  onTransferHistoryDetailsClick(item: TransferHistoryEntry) {
    this.jsonDialogService.showJsonDetailDialog(
      {
        title: item.assetName ?? item.assetId,
        subtitle: 'Transfer History Details',
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
        console.error('Failed to fetch asset details!', error);
        this.notificationService.showError('Failed to fetch asset details!');
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
