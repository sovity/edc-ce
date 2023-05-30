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
import {AssetDetailDialogData} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {CatalogApiUrlService} from '../../../../core/services/api/catalog-api-url.service';
import {ContractOffer} from '../../../../core/services/models/contract-offer';
import {value$} from '../../../../core/utils/form-group-utils';
import {CatalogBrowserFetchDetailDialogComponent} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserFetchDetailDialogData} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.data';
import {CatalogBrowserPageService} from './catalog-browser-page-service';
import {emptyCatalogBrowserPageData} from './catalog-browser-page.data';

@Component({
  selector: 'catalog-browser-page',
  templateUrl: './catalog-browser-page.component.html',
  styleUrls: ['./catalog-browser-page.component.scss'],
})
export class CatalogBrowserPageComponent implements OnInit, OnDestroy {
  data = emptyCatalogBrowserPageData();
  data$ = new BehaviorSubject(this.data);
  searchText = new FormControl('');
  customProviders = '';
  presetProvidersMessage = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private catalogBrowserPageService: CatalogBrowserPageService,
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
    const ref = this.matDialog.open(AssetDetailDialogComponent, {data});
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
