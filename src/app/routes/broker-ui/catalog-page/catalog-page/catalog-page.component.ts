import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {PageEvent} from '@angular/material/paginator';
import {BehaviorSubject, Subject} from 'rxjs';
import {map, takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {CatalogPageSortingItem} from '@sovity.de/broker-server-client';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogResult} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-result';
import {AssetDetailDialogComponent} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.component';
import {FilterValueSelectItem} from '../filter-value-select/filter-value-select-item';
import {FilterValueSelectVisibleState} from '../filter-value-select/filter-value-select-visible-state';
import {CatalogActiveFilterPill} from '../state/catalog-active-filter-pill';
import {CatalogPage} from '../state/catalog-page-actions';
import {CatalogPageState} from '../state/catalog-page-state';
import {CatalogPageStateModel} from '../state/catalog-page-state-model';
import {BrokerDataOffer} from './mapping/broker-data-offer';

@Component({
  selector: 'catalog-page',
  templateUrl: './catalog-page.component.html',
})
export class CatalogPageComponent implements OnInit, OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  @HostBinding('class.p-[20px]')
  @HostBinding('class.space-x-[20px]')
  cls = true;

  state!: CatalogPageStateModel;
  searchText = new FormControl('');
  sortBy = new FormControl<CatalogPageSortingItem | null>(null);
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private matDialog: MatDialog,
    private store: Store,
  ) {}

  ngOnInit(): void {
    this.store.dispatch(CatalogPage.Reset);
    this.startListeningToStore();
    this.startEmittingSearchText();
    this.startEmittingSortBy();
  }

  private startListeningToStore() {
    this.store
      .select<CatalogPageStateModel>(CatalogPageState)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        if (this.searchText.value != state.searchText) {
          this.searchText.setValue(state.searchText);
        }
        if (this.sortBy.value?.sorting !== state.activeSorting?.sorting) {
          this.sortBy.setValue(state.activeSorting);
        }
      });
  }

  private startEmittingSearchText() {
    this.searchText.valueChanges
      .pipe(map((value) => value ?? ''))
      .subscribe((searchText) => {
        if (searchText != this.state.searchText) {
          this.store.dispatch(new CatalogPage.UpdateSearchText(searchText));
        }
      });
  }

  private startEmittingSortBy() {
    this.sortBy.valueChanges
      .pipe(map((value) => value ?? null))
      .subscribe((value) => {
        if (value?.sorting !== this.state.activeSorting?.sorting) {
          this.store.dispatch(new CatalogPage.UpdateSorting(value));
        }
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

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  onSelectedItemsChange(
    filter: FilterValueSelectVisibleState,
    newSelectedItems: FilterValueSelectItem[],
  ) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSelectedItems(
        filter.model.id,
        newSelectedItems,
      ),
    );
  }

  onSearchTextChange(
    filter: FilterValueSelectVisibleState,
    newSearchText: string,
  ) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSearchText(filter.model.id, newSearchText),
    );
  }

  onRemoveActiveFilterItem(item: CatalogActiveFilterPill) {
    this.store.dispatch(new CatalogPage.RemoveActiveFilterItem(item));
  }

  onPageChange(event: PageEvent) {
    this.store.dispatch(new CatalogPage.UpdatePage(event.pageIndex));
  }
}
