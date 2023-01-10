import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, sampleTime } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CatalogBrowserService } from "../../services/catalog-browser.service";
import { CatalogApiUrlService } from "../../services/catalog-api-url.service";
import { ContractOffer } from "../../models/contract-offer";
import { Asset } from "../../models/asset";
import { MatDialog } from "@angular/material/dialog";
import { AssetDetailDialogData } from '../asset-detail-dialog/asset-detail-dialog-data';
import { AssetDetailDialog } from '../asset-detail-dialog/asset-detail-dialog.component';
import { AssetDetailDialogResult } from '../asset-detail-dialog/asset-detail-dialog-result';


@Component({
  selector: 'edc-demo-catalog-browser',
  templateUrl: './catalog-browser.component.html',
  styleUrls: ['./catalog-browser.component.scss']
})
export class CatalogBrowserComponent implements OnInit {

  filteredContractOffers: ContractOffer[] = [];
  filteredContractOfferAssets: Asset[] = [];

  searchText = '';
  customCatalogUrl = '';
  presetCatalogUrlsMessage = ''
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private catalogBrowserService: CatalogBrowserService,
    private catalogApiUrlService: CatalogApiUrlService,
    private matDialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    this.fetch$
      .pipe(
        sampleTime(200),
        switchMap(() => this.catalogBrowserService.getContractOffers()),
        map(contractOffers => {
          const searchText = this.searchText.toLowerCase();
          return contractOffers.filter(contractOffer => contractOffer.asset.name?.toLowerCase()?.includes(searchText));
        })
      )
      .subscribe(filteredContractOffers => {
        this.filteredContractOffers = filteredContractOffers;
        this.filteredContractOfferAssets = filteredContractOffers.map(it => it.asset);
      });
    this.presetCatalogUrlsMessage = this.buildPresetCatalogUrlsMessage()
  }

  onSearch() {
    this.fetch$.next(null);
  }

  onContractOfferClick(contractOffer: ContractOffer) {
    const data = AssetDetailDialogData.forContractOffer(contractOffer);
    const ref = this.matDialog.open(AssetDetailDialog, { data })
    ref.afterClosed().subscribe((result: AssetDetailDialogResult) => {
      if (result.refreshList) {
        this.fetch$.next(null)
      }
    })
  }

  onCatalogUrlsChange(): void {
    this.catalogApiUrlService.setCustomApiUrlString(this.customCatalogUrl)
    this.fetch$.next(null)
  }

  private buildPresetCatalogUrlsMessage(): string {
    const urls = this.catalogApiUrlService.getPresetApiUrls()
    if (!urls.length) {
      return '';
    }
    return `Already using${urls.length > 1 ? ` (${urls.length})` : ''}: ${urls.join(", ")}`;
  }
}
