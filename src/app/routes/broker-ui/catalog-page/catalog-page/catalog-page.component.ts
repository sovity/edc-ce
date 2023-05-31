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
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogResult} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {value$} from '../../../../core/utils/form-group-utils';
import {emptyCatalogPageStateModel} from './catalog-page-data';
import {CatalogPageDataService} from './catalog-page-data.service';
import {BrokerDataOffer} from './mapping/broker-data-offer';

@Component({
  selector: 'catalog-page',
  templateUrl: './catalog-page.component.html',
  styleUrls: ['./catalog-page.component.scss'],
})
export class CatalogPageComponent implements OnInit, OnDestroy {
  data = emptyCatalogPageStateModel();
  data$ = new BehaviorSubject(this.data);
  searchText = new FormControl('');
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private catalogBrowserPageService: CatalogPageDataService,
    private matDialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.catalogBrowserPageService
      .catalogPageData$(this.fetch$.pipe(sampleTime(200)), this.searchText$())
      .subscribe((data) => {
        this.data = data;
        this.data$.next(data);
      });
  }

  onDataOfferClick(dataOffer: BrokerDataOffer) {
    const data =
      this.assetDetailDialogDataService.brokerDataOfferDetails(dataOffer);
    const ref = this.matDialog.open(AssetDetailDialogComponent, {data});
    ref.afterClosed().subscribe((result: AssetDetailDialogResult) => {
      if (result?.refreshList) {
        this.fetch$.next(null);
      }
    });
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
  }
}
