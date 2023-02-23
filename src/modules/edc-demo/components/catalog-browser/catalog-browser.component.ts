import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  BehaviorSubject,
  Observable,
  Subject,
  distinctUntilChanged,
  sampleTime,
} from 'rxjs';
import {map} from 'rxjs/operators';
import {ContractOffer} from '../../models/contract-offer';
import {CatalogApiUrlService} from '../../services/catalog-api-url.service';
import {ContractOfferService} from '../../services/contract-offer.service';
import {value$} from '../../utils/form-group-utils';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';
import {CatalogBrowserFetchDetailDialogComponent} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserFetchDetailDialogData} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.data';
import {CatalogBrowserPageService} from './catalog-browser-page-service';
import {emptyCatalogBrowserPageData} from './catalog-browser-page.data';

@Component({
  selector: 'edc-demo-catalog-browser',
  templateUrl: './catalog-browser.component.html',
  styleUrls: ['./catalog-browser.component.scss'],
})
export class CatalogBrowserComponent implements OnInit, OnDestroy {
  data = emptyCatalogBrowserPageData();
  data$ = new BehaviorSubject(this.data);
  searchText = new FormControl('');
  customProviders = '';
  presetProvidersMessage = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private catalogBrowserPageService: CatalogBrowserPageService,
    private catalogBrowserService: ContractOfferService,
    private catalogApiUrlService: CatalogApiUrlService,
    private matDialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.catalogBrowserPageService
      .contractOfferPageData$(
        this.fetch$.pipe(sampleTime(200)),
        this.searchText$(),
      )
      .subscribe((data) => {
        this.data = data;
        this.data$.next(data);
      });
    this.presetProvidersMessage = this.buildPresetCatalogUrlsMessage();
  }

  onContractOfferClick(contractOffer: ContractOffer) {
    const data = AssetDetailDialogData.forContractOffer(contractOffer);
    const ref = this.matDialog.open(AssetDetailDialog, {data});
    ref.afterClosed().subscribe((result: AssetDetailDialogResult) => {
      if (result?.refreshList) {
        this.fetch$.next(null);
      }
    });
  }

  onShowFetchDetails() {
    const data: CatalogBrowserFetchDetailDialogData = {
      data$: this.data$,
      refresh: () => this.fetch$.next(null),
    };
    this.matDialog.open(CatalogBrowserFetchDetailDialogComponent, {data});
  }

  onCatalogUrlsChange(): void {
    this.catalogApiUrlService.setCustomProvidersAsString(this.customProviders);
    this.fetch$.next(null);
  }

  private buildPresetCatalogUrlsMessage(): string {
    const urls = this.catalogApiUrlService.getPresetProviders();
    if (!urls.length) {
      return '';
    }
    return `Already using${
      urls.length > 1 ? ` (${urls.length})` : ''
    }: ${urls.join(', ')}`;
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();

    // Reset selected Connector Endpoints
    this.catalogApiUrlService.setCustomProviders([]);
  }
}
