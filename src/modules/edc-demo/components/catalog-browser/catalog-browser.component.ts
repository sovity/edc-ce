import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, Subject, sampleTime} from 'rxjs';
import {map, switchMap, takeUntil} from 'rxjs/operators';
import {ContractOffer} from '../../models/contract-offer';
import {Fetched} from '../../models/fetched';
import {CatalogApiUrlService} from '../../services/catalog-api-url.service';
import {CatalogBrowserService} from '../../services/catalog-browser.service';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialogResult} from '../asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';

export interface ContractOfferList {
  filteredContractOffers: ContractOffer[];
  numTotalContractOffer: number;
}

@Component({
  selector: 'edc-demo-catalog-browser',
  templateUrl: './catalog-browser.component.html',
  styleUrls: ['./catalog-browser.component.scss'],
})
export class CatalogBrowserComponent implements OnInit, OnDestroy {
  contractOffersList: Fetched<ContractOfferList> = Fetched.empty();
  searchText = '';
  customProviders = '';
  presetProvidersMessage = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private catalogBrowserService: CatalogBrowserService,
    private catalogApiUrlService: CatalogApiUrlService,
    private matDialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        sampleTime(200),
        switchMap(
          (): Observable<Fetched<ContractOfferList>> =>
            this.catalogBrowserService.getContractOffers().pipe(
              map(
                (contractOffers): ContractOfferList => ({
                  filteredContractOffers: contractOffers.filter(
                    (contractOffer) =>
                      contractOffer.asset.name
                        ?.toLowerCase()
                        ?.includes(this.searchText.toLowerCase()),
                  ),
                  numTotalContractOffer: contractOffers.length,
                }),
              ),
              Fetched.wrap({
                failureMessage: 'Failed fetching contract offers.',
              }),
            ),
        ),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((contractOffersList) => {
        this.contractOffersList = contractOffersList;
      });
    this.presetProvidersMessage = this.buildPresetCatalogUrlsMessage();
  }

  onSearch() {
    this.fetch$.next(null);
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

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();

    // Reset selected Connector Endpoints
    this.catalogApiUrlService.setCustomProviders([]);
  }
}
